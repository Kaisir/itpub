define(function(require, exports, module) {
    var tpl = require('text!publicVueComponent/ggsqbwh/jtjjqk/jtjjqkTpl.html');
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
                    hasDatas: true
                };
            },
            props: ['xsbh', 'editFlag', 'zldm'],
            created: function() {
                var self = this;
                //渲染页面
                this.getModel().then(self.getFormValue);
            },
            methods: {
                getModel: function() {
                    var dfd = $.Deferred();
                    //获取表单模型
                    var self = this;
                    publicUtils.getFZModel('cxxsdjtjjqkbd', 'JTJJQKFZ', self.zldm).done(function(result) {
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
                            url: SWROOT_PATH + "/sqszpubjs/apply/cxxsdjtjjqkbd.do",
                            params: queryParam,
                            name: "cxxsdjtjjqkbd"
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
                save: function() {
                    mintUI.Toast({
                        message: '保存成功',
                        iconClass: 'iconfont mint-icon-i icon-chenggong'
                    });
                    var retParam = {};
                    retParam.FZDM = 'JTJJQKFZ';
                    this.$emit('save', retParam);
                    this.$router.go(-1);
                }
            }
        };
        return page;
    };

});