define(function(require, exports, module) {
    var tpl = require('text!publicVueComponent/ggsqbwh/tdqk/tdqkTpl.html');
    var publicUtils = require('publicVueComponent/ggsqbwh/publicUtils');

    return function() {
    	var page = {
                template: tpl,
                data: function() {
                    return {
                        //团队情况是否可维护
                        tdqksfkwh: false,
                        //团队情况
                        tdqkArray: [],
                        //是否有学生干团队情况记录
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
                props: ['xsbh', 'editFlag', 'zldm', 'xzxn'],
                created: function() {
                    var self = this;
                    self.getFZModel().then(self.getFZModelSFKWH).then(self.getTdqkRecords);
                },
                methods: {
                    //判断表单是否可维护
                    getFZModelSFKWH: function() {
                        var dfd = $.Deferred();
                        var self = this;
                        publicUtils.getFZModelSFKWH('TDQKFZ', self.zldm).done(function(flag) {
                            self.tdqksfkwh = flag;
                            dfd.resolve();
                        });
                        return dfd;
                    },
                    getFZModel: function() {
                        var dfd = $.Deferred();
                        //获取表单模型
                        var self = this;
                        publicUtils.getFZModel('sqsz_cxxstdqkbd', 'TDQKFZ', self.zldm).done(function(result) {
                            self.model = result;
                            dfd.resolve();
                        });
                        return dfd;
                    },
                    //获取团队情况记录
                    getTdqkRecords: function() {
                        var self = this;
                        //参数设置
                        var querySetting = [];
                        querySetting.push({
                            "caption": "",
                            "name": "XSBH",
                            "value": self.xsbh,
                            "builder": "equal",
                            "linkOpt": "AND"
                        });
                        var param = {};
                        param['querySetting'] = JSON.stringify(querySetting);
                        MOB_UTIL.doActionQuery({
                            url: SWROOT_PATH + "/sqszpubjs/apply/sqsz_cxxstdqkbd.do",
                            params: param,
                            name: "sqsz_cxxstdqkbd"
                        }).done(function(result) {
                            if (result && result.length > 0) {
                                for (var j = 0; j < result.length; j++) {
                                    result[j].formName = "form" + j;
                                    result[j].formRead = true;
                                }
                                self.tdqkArray = result;
                                self.hasDatas = true;
                            } else {
                                self.hasDatas = false;
                            }
                            self.dataReady = true;
                        });
                    },
                    //添加团队情况
                    addTdqk: function() {
                        //新增状态再点之间返回
                        if (this.isAdd) {
                            return;
                        }
                        this.formValue = {};
                        this.isAdd = true;
                    },
                    //取消团队情况
                    cancelAdd: function() {
                        this.isAdd = false;
                        this.formValue = {};
                    },
                    //保存新增团队情况
                    saveAdd: function() {
                        if (!this.$refs.formAdd.validate()) {
                            return false;
                        }
                        this.formValue.XSBH = this.xsbh;
                        var self = this;
                        
                        
                        var paramObj = {};
                        paramObj.DATA = JSON.stringify(self.formValue);
                        paramObj.DATA_MODEL = "T_XG_XS_TDQK";
                        //新增
                        paramObj.ACTION_TYPE = "ADD";
                        
                        MOB_UTIL.doActionPostExecute({
                            params: paramObj
                        }).then(function(result) {
                        	mintUI.Toast({
                                message: '保存成功',
                                iconClass: 'iconfont mint-icon-i icon-chenggong'
                            });
                            //获取团队情况记录
                            self.getTdqkRecords();
                            self.isEdit = false;
                            self.isAdd = false;
                            //清空
                            self.formValue = {};
                        });
                    },
                    //删除团队情况
                    deleteTdqk: function(tdqk) {
                    	 //编辑状态再点之间返回
                        if (this.isEdit || this.isAdd) {
                            return;
                        }
                        var self = this;
                        var wid = tdqk.WID;
                        var param = {};
                        param['WID'] = wid;
                        mintUI.MessageBox.confirm('确定要删除该记录吗?', '提示').then(function() {
                            //删除
                            MOB_UTIL.doActionExecute({
                                url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_XS_TDQK_DELETE.do",
                                params: param,
                                name: "T_XG_XS_TDQK_DELETE"
                            }).done(function(result) {
                                //获取团队情况记录
                                self.getTdqkRecords();
                                mintUI.Toast({
                                    message: '删除成功',
                                    iconClass: 'iconfont mint-icon-i icon-chenggong'
                                });
                            });
                        }, function() {});

                    },
                    //编辑团队情况
                    editTdqk: function(index) {
                        //编辑状态再点之间返回
                        if (this.isEdit || this.isAdd) {
                            return;
                        }
                        this.isEdit = true;
                        this.oldEditFormValue = this.copyObj(this.tdqkArray[index]);
                        this.tdqkArray[index].formRead = false;
                    },
                    //取消编辑
                    cancelEdit: function(index) {
                        this.isEdit = false;
                        this.tdqkArray[index].formRead = true;
                        this.tdqkArray[index] = this.oldEditFormValue;
                    },
                    //保存编辑
                    saveEdit: function(index) {
                        var currentForm = 'form' + index;
                        if (!this.$refs[currentForm][0].validate()) {
                            return false;
                        }
                        var currentFormValue = this.tdqkArray[index];
                        var paramObj = {};
                        paramObj.DATA = JSON.stringify(currentFormValue);
                        paramObj.DATA_MODEL = "T_XG_XS_TDQK";
                        //新增
                        paramObj.ACTION_TYPE = "SAVE";
                        var self = this;
                        MOB_UTIL.doActionPostExecute({
                            params: paramObj
                        }).then(function(result) {
                        	mintUI.Toast({
                                message: '保存成功',
                                iconClass: 'iconfont mint-icon-i icon-chenggong'
                            });
                            self.isEdit = false;
                            self.isAdd = false;
                            self.getTdqkRecords();
                        });
                    },
                    //深拷贝
                    copyObj: function(obj) {
                        var res = {};
                        for (var key in obj) {
                            res[key] = obj[key];
                        }
                        return res;
                    },
                    //确认
                    confirm: function() {
                        var retParam = {};
                        retParam.FZDM = 'TDQKFZ';
                        this.$emit('save', retParam);
                        this.$router.go(-1);
                    }
                }
            };
            return page;
    };

});