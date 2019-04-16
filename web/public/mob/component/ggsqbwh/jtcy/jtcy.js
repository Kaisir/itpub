define(function(require, exports, module) {
    var tpl = require('text!publicVueComponent/ggsqbwh/jtcy/jtcyTpl.html');
    var publicUtils = require('publicVueComponent/ggsqbwh/publicUtils');

    return function() {
        var page = {
            template: tpl,
            data: function() {
                return {
                    //家庭成员是否可维护
                    jtcysfkwh: false,
                    //家庭成员记录
                    jtcyArray: [],
                    //是否有家庭成员记录
                    hasDatas: true,
                    //表单模型
                    model: {},
                    formValue: {},
                    customVm: {},
                    readonly: false,
                    //是否处于新增模式
                    isAdd: false,
                    //是否处于编辑模式
                    isEdit: false,
                    dataReady: false,
                    //用于保存编辑状态时，原表单数据
                    oldEditFormValue: {}
                };
            },
            props: ['xsbh', 'editFlag', 'zldm'],
            created: function() {
                var self = this;
                //页面初始化
                self.getFZModelSFKWH().then(self.getJtcyRecords);
            },
            methods: {
                //判断表单是否可维护
                getFZModelSFKWH: function() {
                    var dfd = $.Deferred();
                    var self = this;
                    publicUtils.getFZModelSFKWH('JTCYFZ', self.zldm).done(function(flag) {
                        self.jtcysfkwh = flag;
                        if (self.jtcysfkwh) {
                            //获取表单模型
                            publicUtils.getFZModel('sqsz_cxxsjtcybd', 'JTCYFZ', self.zldm).done(function(result) {
                                self.model = result;
                                dfd.resolve();
                            });
                        }
                    });
                    return dfd;
                },
                //查询已记录的家庭成员
                getJtcyRecords: function() {
                    var self = this;
                    var param = {};
                    param['XSBH'] = this.xsbh;
                    MOB_UTIL.doActionQuery({
                        url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_JBXX_JTCY_QUERY.do",
                        params: param,
                        name: "T_XG_JBXX_JTCY_QUERY"
                    }).done(function(result) {
                        if (result && result.length > 0) {
                            for (var j = 0; j < result.length; j++) {
                                result[j].formName = "form" + j;
                                result[j].formRead = true;
                            }
                            self.jtcyArray = result;
                            self.hasDatas = true;
                        } else {
                            self.hasDatas = false;
                        }
                        self.dataReady = true;
                    });
                },
                //删除家庭成员
                deleteJtcy: function(jtcy) {
                    var wid = jtcy.WID;
                    var param = {};
                    param['WID'] = wid;
                    var oldValue = jtcy;
                    var oldValue2 = {};
                    delete oldValue.WID;
                    delete oldValue.TBRQ;
                    delete oldValue.TBLX;
                    delete oldValue.CZRQ;
                    delete oldValue.CZZ;
                    delete oldValue.CZZXM;
                    delete oldValue.formValue;
                    for (var key in oldValue) {
                        if (key == "XSBH") {
                            oldValue2[key] = oldValue[key];
                        }
                        oldValue2[key + "OLD"] = oldValue[key];
                    }
                    var self = this;
                    mintUI.MessageBox.confirm('确定要删除该条成员记录吗?', '提示').then(function() {
                        //获取新生成的WID
                        publicUtils.generateWId().done(function(wid) {
                            oldValue2.WID = wid;
                            //插入修改日志
                            MOB_UTIL.doActionExecute({
                                url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_JBXX_JTCY_XGRZ_ADD.do",
                                params: oldValue2,
                                name: "T_XG_JBXX_JTCY_XGRZ_ADD"
                            }).done(function(result) {});
                            //删除
                            MOB_UTIL.doActionExecute({
                                url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_JBXX_JTCY_DELETE.do",
                                params: param,
                                name: "T_XG_JBXX_JTCY_DELETE"
                            }).done(function(result) {
                                self.getJtcyRecords();
                                mintUI.Toast({
                                    message: '删除成功',
                                    iconClass: 'iconfont mint-icon-i icon-chenggong'
                                });
                            });
                        });
                    }, function() {});
                },
                //编辑家庭成员
                editJtcy: function(index) {
                    //编辑状态再点之间返回
                    if (this.isEdit || this.isAdd) {
                        return;
                    }
                    this.isEdit = true;
                    this.oldEditFormValue = this.copyObj(this.jtcyArray[index]);
                    this.jtcyArray[index].formRead = false;
                },
                //取消编辑
                cancelEdit: function(index) {
                    this.isEdit = false;
                    this.jtcyArray[index].formRead = true;
                    this.jtcyArray[index] = this.oldEditFormValue;
                },
                //保存编辑
                saveEdit: function(index) {
                    var currentForm = 'form' + index;
                    if (!this.$refs[currentForm][0].validate()) {
                        return false;
                    }
                    var self = this;
                    var currentFormValue = this.jtcyArray[index];
                    //修改保存
                    MOB_UTIL.doActionExecute({
                        url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_JBXX_JTCY_SAVE.do",
                        params: currentFormValue,
                        name: "T_XG_JBXX_JTCY_SAVE"
                    }).done(function(result) {
                        mintUI.Toast({
                            message: '保存成功',
                            iconClass: 'iconfont mint-icon-i icon-chenggong'
                        });
                        self.isEdit = false;
                        self.isAdd = false;
                        self.getJtcyRecords();
                    });
                },
                //添加家庭成员
                addJtcy: function() {
                    //编辑状态再点之间返回
                    if (this.isAdd) {
                        return;
                    }
                    this.formValue = {};
                    this.isAdd = true;
                },
                //取消新增
                cancelAdd: function() {
                    this.isAdd = false;
                    //清空
                    this.formValue = {};
                },
                //保存新增
                saveAdd: function() {
                    if (!this.$refs.formAdd.validate()) {
                        return false;
                    }
                    var self = this;
                    self.formValue.XSBH = self.xsbh;
                    publicUtils.generateWId().done(function(wid) {
                        //插入修改日志
                        MOB_UTIL.doActionExecute({
                            url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_JBXX_JTCY_XGRZ_ADD.do",
                            params: self.formValue,
                            name: "T_XG_JBXX_JTCY_XGRZ_ADD"
                        }).done(function(result) {});
                        //赋值WID
                        self.formValue.WID = wid;
                        //新增
                        MOB_UTIL.doActionExecute({
                            url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_JBXX_JTCY_ADD.do",
                            params: self.formValue,
                            name: "T_XG_JBXX_JTCY_ADD"
                        }).done(function(result) {
                            mintUI.Toast({
                                message: '保存成功',
                                iconClass: 'iconfont mint-icon-i icon-chenggong'
                            });
                            self.getJtcyRecords();
                            self.isEdit = false;
                            self.isAdd = false;
                            //清空
                            self.formValue = {};
                        });
                    });
                },
                //深拷贝
                copyObj: function(obj) {
                    var res = {}
                    for (var key in obj) {
                        res[key] = obj[key]
                    }
                    return res;
                },
                //确认
                confirm: function() {
                    var retParam = {};
                    retParam.FZDM = 'JTCYFZ';
                    this.$emit('save', retParam);
                    this.$router.go(-1);
                }
            }
        };
        return page;
    };

});