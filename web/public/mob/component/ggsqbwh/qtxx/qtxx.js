define(function(require, exports, module) {
    var tpl = require('text!publicVueComponent/ggsqbwh/qtxx/qtxxTpl.html');
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
            props: ['mkdm', 'xsbh', 'editFlag', 'zldm', 'qtxxObj'],
            created: function() {
                var self = this;
                self.readonly = !self.editFlag;
                //页面初始化
                self.getFZModel().then(self.getFZModelSz).then(self.getFzValue);
                if (self.qtxxObj) {
                    self.formValue = self.qtxxObj;
                }
            },
            methods: {
                //获取表单模型
                getFZModel: function() {
                    var dfd = $.Deferred();
                    //获取表单模型
                    var self = this;
                    self.getActionName();
                    publicUtils.getFZModel(self.actionName, 'QTXXFZ', self.zldm).done(function(result) {
                        self.model = result;
                        dfd.resolve();
                    });
                    return dfd;
                },
                //获取分组模型的设置
                getFZModelSz: function() {
                    var dfd = $.Deferred();
                    var queryParam = {};
                    queryParam['ZLDM'] = this.zldm;
                    queryParam['FZDM'] = 'QTXXFZ';
                    queryParam['SFSY'] = '1';
                    MOB_UTIL.doActionQuery({
                        url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_FZZD_SZ_QUERY.do",
                        params: queryParam,
                        name: "T_XG_FZZD_SZ_QUERY"
                    }).done(function(result) {
                        dfd.resolve();
                    });
                    return dfd;
                },
                //获取表单值(重新提交/编辑的时候使用)
                getFzValue: function() {
                    var self = this;
                    self.dataReady = true;
                    //TODO,需要考虑重新提交时候
                },
                getActionName: function() {
                    if (this.mkdm == 'ZXJ') {
                        this.actionName = 'cxzxjqtxxbd';
                    }
                },
                saveQtxx: function() {
                    if (!this.$refs.form.validate()) {
                        return false;
                    }
                    var self = this;
                    mintUI.Toast({
                        message: '保存成功',
                        iconClass: 'iconfont mint-icon-i icon-chenggong'
                    });
                    var retParam = {};
                    retParam.FZDM = 'QTXXFZ';
                    retParam.QTXX_VALUE = self.formValue;
                    self.$emit('save', retParam);
                    self.$router.go(-1);
                }
            }
        };
        return page;
    };

});