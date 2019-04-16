define(function(require, exports, module) {
    var tpl = require('text!publicVueComponent/ggsqbwh/zzcl/zzclTpl.html');
    var publicUtils = require('publicVueComponent/ggsqbwh/publicUtils');

    return function() {
        var page = {
            template: tpl,
            data: function() {
                return {
                    model: [],
                    formValue: {},
                    customVm: {},
                    readonly: true,
                    dataReady: false,
                    actionName: ''
                };
            },
            props: ['mkdm', 'xsbh', 'editFlag', 'zldm', 'fjObj'],
            created: function() {
                var self = this;
                self.readonly = !self.editFlag;
                //页面初始化
                self.getFZModel().then(self.getFzValue);
                if (self.fjObj) {
                    self.formValue = self.fjObj;
                }
            },
            methods: {
                //获取表单模型
                getFZModel: function() {
                    var dfd = $.Deferred();
                    //获取表单模型
                    var self = this;
                    self.getActionName();
                    publicUtils.getFZModel(self.actionName, 'FJFZ', self.zldm).done(function(result) {
                        self.model = result;
                        // if (!self.readonly) {
                        self.formatFormModel();
                        // }
                        dfd.resolve();
                    });
                    return dfd;
                },
                //获取表单值(重新提交/编辑的时候使用)
                getFzValue: function() {
                    var self = this;
                    self.dataReady = true;
                },
                getActionName: function() {
                    if (this.mkdm == 'JXJ') {
                        this.actionName = 'cxjxjfjsqbd';
                    }
                    if (this.mkdm == 'ZXJ') {
                        this.actionName = 'cxsqfjbd';
                    }
                    if (this.mkdm == 'RYCH') {
                        this.actionName = 'cxrychfjbd';
                    }
                },
                saveZzcl: function() {
                    if (!this.$refs.form.validate()) {
                        return false;
                    }
                    var self = this;
                    mintUI.Toast({
                        message: '保存成功',
                        iconClass: 'iconfont mint-icon-i icon-chenggong'
                    });
                    var retParam = {};
                    retParam.FZDM = 'FJFZ';
                    retParam.FJ_VALUE = self.formValue;
                    self.$emit('save', retParam);
                    self.$router.go(-1);
                },
                //格式化表单模型,切换附件字段的展现形式
                formatFormModel: function() {
                    var self = this;
                    for (var index in self.model) {
                        if (self.model[index].name == "FJ") {
                            // if (self.model[index].xtype == 'uploadsingleimage') {
                            //     self.model[index].xtype = 'cache-upload';
                            // } else {
                            self.model[index].xtype = 'uploadsingleimage';
                            // }
                        }
                    }
                }
            }
        };
        return page;
    };

});