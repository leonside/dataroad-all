package com.leonside.dataroad.flink.inputformat;

import com.leonside.dataroad.common.context.RestoreConfig;
import com.leonside.dataroad.common.utils.ExceptionUtil;
import com.leonside.dataroad.flink.inputformat.ErrorInputSplit;
import com.leonside.dataroad.flink.metric.AccumulatorCollector;
import com.leonside.dataroad.flink.metric.BaseMetric;
import com.leonside.dataroad.flink.metric.Metrics;
import com.leonside.dataroad.flink.metric.ByteRateLimiter;
import com.leonside.dataroad.flink.restore.FormatState;
import org.apache.flink.api.common.accumulators.LongCounter;
import org.apache.flink.api.common.io.DefaultInputSplitAssigner;
import org.apache.flink.api.common.io.RichInputFormat;
import org.apache.flink.api.common.io.statistics.BaseStatistics;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.io.InputSplit;
import org.apache.flink.core.io.InputSplitAssigner;
import org.apache.flink.types.Row;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author leon
 */
public abstract class GenericRichInputFormat extends RichInputFormat<Row, InputSplit> {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected String jobName = "defaultJobName";
    protected String jobId;
    protected LongCounter numReadCounter;
    protected LongCounter bytesReadCounter;
    protected LongCounter durationCounter;
    protected String monitorUrls;
    protected long bytes;
    protected ByteRateLimiter byteRateLimiter;

    protected RestoreConfig restoreConfig;

    protected FormatState formatState;

    protected transient BaseMetric inputMetric;

    protected int indexOfSubTask;

    protected long startTime;

    protected AccumulatorCollector accumulatorCollector;

    private boolean inited = false;

    private AtomicBoolean isClosed = new AtomicBoolean(false);

    @Override
    public void configure(Configuration configuration) {

    }

    @Override
    public void openInputFormat() throws IOException {
        initJobInfo();
        startTime = System.currentTimeMillis();
    }

    private void initJobInfo() {
        Map<String, String> vars = getRuntimeContext().getMetricGroup().getAllVariables();
        if(vars != null && vars.get(Metrics.JOB_NAME) != null) {
            jobName = vars.get(Metrics.JOB_NAME);
        }

        if(vars!= null && vars.get(Metrics.JOB_ID) != null) {
            jobId = vars.get(Metrics.JOB_ID);
        }

        if(vars != null && vars.get(Metrics.SUBTASK_INDEX) != null){
            indexOfSubTask = Integer.parseInt(vars.get(Metrics.SUBTASK_INDEX));
        }
    }

    @Override
    public InputSplit[] createInputSplits(int i) throws IOException {
        try {
            return doCreateInputSplits(i);
        } catch (Exception e){
            LOG.warn(ExceptionUtil.getErrorMessage(e));

            return createErrorInputSplit(e);
        }
    }

    private ErrorInputSplit[] createErrorInputSplit(Exception e){
        ErrorInputSplit[] inputSplits = new ErrorInputSplit[1];
        ErrorInputSplit errorInputSplit = new ErrorInputSplit(ExceptionUtil.getErrorMessage(e));
        inputSplits[0] = errorInputSplit;

        return inputSplits;
    }

    /**
     * 由子类实现，创建数据分片
     *
     * @param i 分片数量
     * @return 分片数组
     * @throws Exception 可能会出现连接数据源异常
     */
    protected abstract InputSplit[] doCreateInputSplits(int i) throws Exception;

    public FormatState getFormatState() {
        if (formatState != null && numReadCounter != null && inputMetric!= null) {
            formatState.setMetric(inputMetric.getMetricCounters());
        }
        return formatState;
    }

    @Override
    public BaseStatistics getStatistics(BaseStatistics baseStatistics) throws IOException {
        return null;
    }

    @Override
    public final InputSplitAssigner getInputSplitAssigner(InputSplit[] inputSplits) {
        return new DefaultInputSplitAssigner(inputSplits);
    }

    @Override
    public void open(InputSplit inputSplit) throws IOException {
        checkIfCreateSplitFailed(inputSplit);

        if(!inited){
            initAccumulatorCollector();
            initStatisticsAccumulator();
            openByteRateLimiter();
            initRestoreInfo();

            if(restoreConfig.isRestore()){
                formatState.setNumOfSubTask(indexOfSubTask);
            }

            inited = true;
        }

        doOpen(inputSplit);
    }

    private void initStatisticsAccumulator(){
        numReadCounter = getRuntimeContext().getLongCounter(Metrics.NUM_READS);
        bytesReadCounter = getRuntimeContext().getLongCounter(Metrics.READ_BYTES);
        durationCounter = getRuntimeContext().getLongCounter(Metrics.READ_DURATION);

        inputMetric = new BaseMetric(getRuntimeContext());
        inputMetric.addMetric(Metrics.NUM_READS, numReadCounter, true);
        inputMetric.addMetric(Metrics.READ_BYTES, bytesReadCounter, true);
        inputMetric.addMetric(Metrics.READ_DURATION, durationCounter);
    }

    /**
     * 有子类实现，打开数据连接
     *
     * @param inputSplit 分片
     * @throws IOException 连接异常
     */
    protected abstract void doOpen(InputSplit inputSplit) throws IOException;


    private void initAccumulatorCollector(){
        String lastWriteLocation = String.format("%s_%s", Metrics.LAST_WRITE_LOCATION_PREFIX, indexOfSubTask);
        String lastWriteNum = String.format("%s_%s", Metrics.LAST_WRITE_NUM__PREFIX, indexOfSubTask);

        accumulatorCollector = new AccumulatorCollector(jobId, monitorUrls, getRuntimeContext(), 2,
                Arrays.asList(Metrics.NUM_READS,
                        Metrics.READ_BYTES,
                        Metrics.READ_DURATION,
                        Metrics.WRITE_BYTES,
                        Metrics.NUM_WRITES,
                        lastWriteLocation,
                        lastWriteNum));
        accumulatorCollector.start();
    }

    private void openByteRateLimiter(){
        if (this.bytes > 0) {
            this.byteRateLimiter = new ByteRateLimiter(accumulatorCollector, this.bytes);
            this.byteRateLimiter.start();
        }
    }

    private void initRestoreInfo(){
        if(restoreConfig == null){
            restoreConfig = RestoreConfig.defaultConfig();
        } else if(restoreConfig.isRestore()){
            if(formatState == null){
                formatState = new FormatState(indexOfSubTask, null);
            } else {
                numReadCounter.add(formatState.getMetricValue(Metrics.NUM_READS));
                bytesReadCounter.add(formatState.getMetricValue(Metrics.READ_BYTES));
                durationCounter.add(formatState.getMetricValue(Metrics.READ_DURATION));
            }
        }
    }

    private void checkIfCreateSplitFailed(InputSplit inputSplit){
        if (inputSplit instanceof ErrorInputSplit) {
            throw new RuntimeException(((ErrorInputSplit) inputSplit).getErrorMessage());
        }
    }

    @Override
    public Row nextRecord(Row row) throws IOException {
        if(byteRateLimiter != null) {
            byteRateLimiter.acquire();
        }
        Row internalRow = doNextRecord(row);
        if(internalRow != null){
//            internalRow = setChannelInformation(internalRow);

            updateDuration();
            if(numReadCounter !=null ){
                numReadCounter.add(1);
            }
            if(bytesReadCounter!=null){
                bytesReadCounter.add(internalRow.toString().getBytes().length);
            }
        }

        return internalRow;
    }

//    private Row setChannelInformation(Row internalRow){
//        Row rowWithChannel = Row.withNames(internalRow.getKind());//new Row(internalRow.getArity() + 1);
//        internalRow.getFieldNames(true).forEach(it->{
//            rowWithChannel.setField(it, internalRow.getField(it));
//        });
//        rowWithChannel.setField("indexOfSubTask", indexOfSubTask);
//        return rowWithChannel;
//    }

    private void updateDuration(){
        if(durationCounter !=null ){
            durationCounter.resetLocal();
            durationCounter.add(System.currentTimeMillis() - startTime);
        }
    }

    /**
     * 由子类实现，读取一条数据
     *
     * @param row 需要创建和填充的数据
     * @return 读取的数据
     * @throws IOException 读取异常
     */
    protected abstract Row doNextRecord(Row row) throws IOException;

    @Override
    public void close() throws IOException {
        try{
            doClose();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeInputFormat() throws IOException {
        if (isClosed.get()) {
            return;
        }

        if(durationCounter != null){
            updateDuration();
        }

        if(byteRateLimiter != null){
            byteRateLimiter.stop();
        }

        if(accumulatorCollector != null){
            accumulatorCollector.close();
        }

        if(inputMetric != null){
            inputMetric.waitForReportMetrics();
        }

        isClosed.set(true);
        LOG.info("subtask input close finished");
    }

    /**
     * 由子类实现，关闭资源
     *
     * @throws IOException 连接关闭异常
     */
    protected abstract  void doClose() throws IOException;

    public RestoreConfig getRestoreConfig() {
        return restoreConfig;
    }

    public void setRestoreState(FormatState formatState) {
        this.formatState = formatState;
    }

}
