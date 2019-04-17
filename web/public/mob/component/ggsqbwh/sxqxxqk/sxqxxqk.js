define(function(require, exports, module) {
    var tpl = require('text!publicVueComponent/ggsqbwh/sxqxxqk/sxqxxqkTpl.html');
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
                    //上学年学习情况是否可维护
                    sxnxxqksfkwh: false,
                    hasDatas: true
                };
            },
            props: ['xsbh', 'editFlag', 'zldm'],
            created: function() {
                var self = this;
                //判断上学年学习情况是否可维护
                this.getXXQKFZModelSFKWH();
                //渲染页面
                this.getModel().then(self.getFormValue);
            },
            methods: {
                //判断上学年学习情况是否可维护
                getXXQKFZModelSFKWH: function() {
                    var self = this;
                    publicUtils.getFZModelSFKWH('SXNXXQKFZ', self.zldm).done(function(flag) {
                        //判断表单是否可编辑
                        self.sxnxxqksfkwh = flag;
                        if (self.sxnxxqksfkwh && self.editFlag) {
                            self.readonly = false;
                        }
                    });
                },
                getModel: function() {
                    var dfd = $.Deferred();
                    //获取表单模型
                    var self = this;
                    publicUtils.getFZModel('cxxsxxqkbd', 'SXNXXQKFZ', self.zldm).done(function(result) {
                        self.model = result;
                        dfd.resolve();
                    });
                    return dfd;
                },
                getFormValue: function() {
                    var queryParam = {};
                    queryParam['XSBH'] = this.xsbh;
                    var self = this;
                    MOB_UTIL.doActionQuery({
                            url: SWROOT_PATH + "/sqszpubjs/apply/cxxsxxqkbd.do",
                            params: queryParam,
                            name: "cxxsxxqkbd"
                        })
                        .done(function(result) {
                            if (result && result.length > 0) {
                                self.formValue = result[0];
                                self.hasDatas = true;
                            } else {
                                self.hasDatas = false;
                            }

                            self.dataReady = true;
                        });
                },
                //保存
                saveXxqk: function() {
                    var self = this;
                    if (self.readonly || !self.hasDatas) {
                        mintUI.Toast({
                            message: '保存成功',
                            iconClass: 'iconfont mint-icon-i icon-chenggong'
                        });
                        var retParam = {};
                        retParam.FZDM = 'SXNXXQKFZ';
                        self.$emit('save', retParam);
                        self.$router.go(-1);
                    } else {
                        if (!self.$refs.form.validate()) {
                            return false;
                        }
                        //修改保存
                        self.formValue['XSBH'] = self.xsbh;
                        MOB_UTIL.doActionExecute({
                            url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_XSCJ_SAVE.do",
                            params: self.formValue,
                            name: "T_XG_XSCJ_SAVE"
                        }).done(function(result) {
                            mintUI.Toast({
                                message: '保存成功',
                                iconClass: 'iconfont mint-icon-i icon-chenggong'
                            });
                            var retParam = {};
                            retParam.FZDM = 'SXNXXQKFZ';
                            self.$emit('save', retParam);
                            self.$router.go(-1);
                        });
                    }
                }
            }
        };
        return page;
    };

});