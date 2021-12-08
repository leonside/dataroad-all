
package com.leonside.dataroad.plugin.es.reader;

import org.apache.flink.core.io.InputSplit;

/**
 * The Class describing each InputSplit of Elasticsearch
 *
 */
public class EsInputSplit implements InputSplit {

    private int from;
    private int size;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public EsInputSplit(int from, int size) {
        this.from = from;
        this.size = size;
    }

    @Override
    public int getSplitNumber() {
        return 0;
    }

}
