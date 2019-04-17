define(function(require, exports, module) {
    var tpl = require('text!publicVueComponent/ggsqbwh/sqcs/sqcsTpl.html');
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
                    actionName: '',
                    //文字提示
                    titleText: '请输入申请陈述',
                    //最小字数
                    zxzs: '',
                    //最大字数
                    zdzs: ''
                };
            },
            props: ['mkdm', 'xsbh', 'editFlag', 'zldm', 'sqcsObj'],
            created: function() {
                var self = this;
                self.readonly = !self.editFlag;
                //页面初始化
                self.getFZModel().then(self.getFZModelSz).then(self.getFzValue);
                if (self.sqcsObj) {
                    self.formValue = self.sqcsObj;
                }
            },
            methods: {
                //获取表单模型
                getFZModel: function() {
                    var dfd = $.Deferred();
                    //获取表单模型
                    var self = this;
                    self.getActionName();
                    publicUtils.getFZModel(self.actionName, 'SQCSFZ', self.zldm).done(function(result) {
                        self.model = result;
                        dfd.resolve();
                    });
                    return dfd;
                },
                //获取分组模型的设置
                getFZModelSz: function() {
                    var dfd = $.Deferred();
                    var self = this;
                    var queryParam = {};
                    queryParam['ZLDM'] = this.zldm;
                    queryParam['FZDM'] = 'SQCSFZ';
                    queryParam['SFSY'] = '1';
                    MOB_UTIL.doActionQuery({
                        url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_FZZD_SZ_QUERY.do",
                        params: queryParam,
                        name: "T_XG_FZZD_SZ_QUERY"
                    }).done(function(result) {
                        if (result && result.length > 0) {
                            if (result[0].ZXZS) {
                                self.zxzs = result[0].ZXZS;
                                self.titleText += ',最小字数为' + result[0].ZXZS + '个字';
                            }
                            if (result[0].ZDZS) {
                                self.zdzs = result[0].ZDZS;
                                self.titleText += ',最大字数为' + result[0].ZDZS;
                                self.model[0].checkSize = result[0].ZDZS;
                            }
                            Vue.nextTick(function() {
                                $('.mint-textarea-core').attr('placeholder', self.titleText);
                            });
                        }
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
                        this.actionName = 'cxjxjsqcsbd';
                    }
                    if (this.mkdm == 'ZXJ') {
                        this.actionName = 'cxsqcsbd';
                    }
                    if (this.mkdm == 'RYCH') {
                        this.actionName = 'cxrychsqcsbd';
                    }
                },
                saveSqcs: function() {
                    if (!this.$refs.form.validate()) {
                        return false;
                    }
                    var self = this;
                    var sqcs = self.formValue.SQLY;
                    if (self.zdzs && self.zdzs < sqcs.length) {
                        mintUI.Toast({
                            message: '字数 不可大于最大字数' + self.zdzs + "个字！",
                            position: 'middle',
                            duration: 2000
                        });
                        return false;
                    }
                    if (self.zxzs && self.zxzs > sqcs.length) {
                        mintUI.Toast({
                            message: '字数不可小于最小字数' + self.zxzs + "个字！",
                            position: 'middle',
                            duration: 2000
                        });
                        return false;
                    }
                    mintUI.Toast({
                        message: '保存成功',
                        iconClass: 'iconfont mint-icon-i icon-chenggong'
                    });
                    var retParam = {};
                    retParam.FZDM = 'SQCSFZ';
                    retParam.SQCS_VALUE = self.formValue;
                    self.$emit('save', retParam);
                    self.$router.go(-1);
                }
            }
        };
        return page;
    };

});