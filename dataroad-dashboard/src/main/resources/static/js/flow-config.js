function FlowConfig() {
    this.deleteConfig = function (data) {
        $.ajax({
            url: window.url + "api/jobflowconfig/" + data.id  ,
            type:"delete",
            contentType:'application/json',
            success:function(data){
                layer.msg("删除成功" );
            },
            error:function(data){
                layer.alert(JSON.stringify(data.responseText), {
                    title: "信息"
                });
            }
        });
    }

    this.showCreateNewConfig = function (formdata) {
        var method = null;
        if(formdata){
            $("[name='id']").val(formdata.id);
            $("[name='id']").attr('readonly', true);
            $("[name='golbalSetting']").val(formdata.golbalSetting);
            $("[name='description']").val(formdata.description);
            method = 'put';
        }else{
            $("[name='id']").val('');
            $("[name='id']").attr('readonly', false);
            $("[name='description']").val('');
            $("[name='golbalSetting']").val('');
            method = 'post';
        }

        layer.open({
            type: 1,
            title: "新建流程配置",
            area: ['50%','85%'],
            content: $('#createConfigContent'),
            cancel:function () {
                $('#createConfigContent').attr("style","display:none;");
            },
            success: function () {
                layui.use('form', function() {
                    var form = layui.form; //只有执行了这一步，部分表单元素才会自动修饰成功
                    form.on('submit(createConfigform)', function(data){
                        $.ajax({
                            url: window.url + "api/jobflowbaseconfig"  ,
                            type: method,
                            dataType:'json',
                            contentType:'application/json',
                            data: JSON.stringify(getFormJson($(data.form))),
                            success:function(data){
                                parent.location.reload();
                                layer.msg("保存成功" ,1000);
                            },
                            error:function(data){
                                layer.alert(JSON.stringify(data.responseText), {
                                    title: "信息"
                                });
                            }
                        });
                        return false;
                    });
                });

            }
        })
    }

    function getFormJson(form) {
        var data = {};
        let values = form.serializeArray()
        values.forEach(function (item) {
            data[item.name] = item.value;
        })
        console.log(data)
        return data;
    }
}