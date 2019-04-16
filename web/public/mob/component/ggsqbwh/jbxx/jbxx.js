define(function(require, exports, module) {
    var tpl = require('text!publicVueComponent/ggsqbwh/jbxx/jbxxTpl.html');
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
                    //基本信息是否可维护
                    jbxxsfkwh: false
                };
            },
            props: ['mkdm', 'xsbh', 'editFlag', 'zldm'],
            created: function() {
                this.readonly = !this.editFlag;
                var self = this;
                //判断基本信息是否可维护
                this.getJBXXFZModelSFKWH();
                //渲染页面
                this.getModel().then(self.getFormValue);
            },
            methods: {
                //判断基本信息是否可维护
                getJBXXFZModelSFKWH: function() {
                    var self = this;
                    publicUtils.getJBXXFZModelSFKWH('JBXXFZ', self.zldm).done(function(flag) {
                        self.jbxxsfkwh = flag;
                    });
                },
                //获取表单模型
                getModel: function() {
                    var self = this;
                    var dfd = $.Deferred();
                    var actionName = self.getJbxxAction();
                    var formModel = WIS_EMAP_SERV.getModel(SWROOT_PATH + "/sqszpubjs/apply.do", actionName, "form");
                    var queryParam = {};
                    queryParam['ZLDM'] = self.zldm;
                    queryParam['FZDM'] = 'JBXXFZ';
                    MOB_UTIL.doActionQuery({
                            url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_FZZD_SZ_QUERY.do",
                            params: queryParam,
                            name: "T_XG_FZZD_SZ_QUERY"
                        })
                        .done(function(result) {
                            if (result && result.length > 0) {
                                for (var i = 0; i < result.length; i++) {
                                    for (var j = 0; j < formModel.length; j++) {
                                        if (result[i].ZDDM == formModel[j].name) {
                                            if (result[i].SFXS == '1') {
                                                formModel[j].hidden = false;
                                            }
                                            if (result[i].SFKG == '1' && self.editFlag) {
                                                formModel[j].readonly = false;
                                            }
                                        }
                                    }
                                }
                            }
                            self.model = formModel;
                            dfd.resolve();
                        });
                    return dfd;
                },
                //获取表单值
                getFormValue: function() {
                    var queryParam = {};
                    queryParam['XSBH'] = this.xsbh;
                    var self = this;
                    var actionName = self.getJbxxAction();
                    MOB_UTIL.doActionQuery({
                            url: SWROOT_PATH + "/sqszpubjs/apply/" + actionName + ".do",
                            params: queryParam,
                            name: actionName
                        })
                        .done(function(result) {
                            self.formValue = result[0];
                            self.dataReady = true;
                            Vue.nextTick(function() {
                                $('.mint-cell-group-title').css('display', 'none');
                            });
                        });
                },
                //获取基本信息动作
                getJbxxAction: function() {
                    var actionName = 'sqsz_cxxsjbxxbd';
                    if (this.mkdm == 'JXJ' || this.mkdm == 'RYCH') {
                        actionName = 'sqsz_jxjcxxsjbxxbd';
                    }
                    return actionName;
                },
                //保存
                saveJbxx: function() {
                    if (!this.$refs.form.validate()) {
                        return false;
                    }
                    var self = this;
                    //保存基本信息
                    if (self.jbxxsfkwh) {
                    	var param2 = {};
                		param2["ZLDM"] = self.zldm;
                		param2["FZDM"] = "JBXXFZ";
                		param2["FormValue"] = self.formValue;
                    	MOB_UTIL.doPost({url: SWROOT_PATH +'/PublicFormApply/savePublicFormJbxx.do',params: param2}).done(function(result) {
                    		//如果可维护，则插入修改日志
                            MOB_UTIL.doActionExecute({
                                url: SWROOT_PATH + "/sqszpubjs/apply/sqsz_crxsxgrz.do",
                                params: { XSBH: self.xsbh },
                                name: "sqsz_crxsxgrz"
                            }).done(function(result) {
                                mintUI.Toast({
                                    message: '保存成功',
                                    iconClass: 'iconfont mint-icon-i icon-chenggong'
                                });
                                var retParam = {};
                                retParam.FZDM = 'JBXXFZ';
                                self.$emit('save', retParam);
                                self.$router.go(-1);
                            });
                        });
                    } else {
                        mintUI.Toast({
                            message: '保存成功',
                            iconClass: 'iconfont mint-icon-i icon-chenggong'
                        });
                        var retParam = {};
                        retParam.FZDM = 'JBXXFZ';
                        self.$emit('save', retParam);
                        self.$router.go(-1);
                    }
                }
            }
        };
        return page;
    };
});