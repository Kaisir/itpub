define(function(require, exports, module) {
    var tpl = require('text!publicVueComponent/ggsqbwh/xsgb/xsgbTpl.html');
    var publicUtils = require('publicVueComponent/ggsqbwh/publicUtils');

    return function() {
        var page = {
            template: tpl,
            data: function() {
                return {
                    //学生干部是否可维护
                    xsgbsfkwh: false,
                    //学生干部记录
                    xsgbArray: [],
                    //是否有学生干部记录
                    hasDatas: true,
                    //数据范围
                    sjfw: null,
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
                self.getFZModel().then(self.getFZModelSFKWH).then(self.getXsgbSjfw);
            },
            methods: {
                //判断表单是否可维护
                getFZModelSFKWH: function() {
                    var dfd = $.Deferred();
                    var self = this;
                    publicUtils.getFZModelSFKWH('XSGBFZ', self.zldm).done(function(flag) {
                        self.xsgbsfkwh = flag;
                        dfd.resolve();
                    });
                    return dfd;
                },
                getFZModel: function() {
                    var dfd = $.Deferred();
                    //获取表单模型
                    var self = this;
                    publicUtils.getFZModel('cxxsgbxxbd', 'XSGBFZ', self.zldm).done(function(result) {
                        self.model = result;
                        dfd.resolve();
                    });
                    return dfd;
                },
                //查询数据范围
                getXsgbSjfw: function() {
                    var self = this;
                    var tjParam = {};
                    tjParam['SFSY'] = '1';
                    tjParam['FZDM'] = 'XSGBFZ';
                    tjParam['ZLDM'] = self.zldm;
                    MOB_UTIL.doActionQuery({
                        url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_FZZD_SZ_QUERY.do",
                        params: tjParam,
                        name: "T_XG_FZZD_SZ_QUERY"
                    }).done(function(result) {
                        if (result && result.length > 0) {
                            for (var i = 0; i < result.length; i++) {
                                // 数据来源
                                if (result[i].ZDDM && result[i].ZDDM == 'SJFW') {
                                    self.sjfw = result[i];
                                }
                            }
                            //获取学生干部记录
                            self.getXsgbRecords();
                        }
                    });
                },
                //获取学生干部记录
                getXsgbRecords: function() {
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
                    var subQs = [];
                    if (self.sjfw && self.sjfw.ZDZ && self.sjfw.ZDZ == '1' && self.xzxn) {
                        subQs.push({
                            "caption": "",
                            "name": "RZKSSJ",
                            "value": parseInt(self.xzxn) - 1,
                            "builder": "lessEqual",
                            "linkOpt": "AND"
                        });
                        subQs.push({
                            "caption": "",
                            "name": "RZJSSJ",
                            "value": parseInt(self.xzxn) - 1,
                            "builder": "moreEqual",
                            "linkOpt": "AND"
                        });
                        //上学年学生干部任职增加任职结束日期为空的条件过滤
                        var subQs2 = [];
                        subQs2.push({
                            "caption": "",
                            "name": "RZKSSJ",
                            "value": parseInt(self.xzxn) - 1,
                            "builder": "lessEqual",
                            "linkOpt": "OR"
                        });
                        subQs2.push({
                            "caption": "",
                            "name": "RZJSSJ",
                            "value": null,
                            "builder": "equal",
                            "linkOpt": "AND"
                        });
                        subQs.push(subQs2);
                    }
                    querySetting.push(subQs);
                    var param = {};
                    param['querySetting'] = JSON.stringify(querySetting);
                    MOB_UTIL.doActionQuery({
                        url: SWROOT_PATH + "/sqszpubjs/apply/cxygxsdbjgbxx.do",
                        params: param,
                        name: "cxygxsdbjgbxx"
                    }).done(function(result) {
                        if (result && result.length > 0) {
                            for (var j = 0; j < result.length; j++) {
                                result[j].formName = "form" + j;
                                result[j].formRead = true;
                            }
                            self.xsgbArray = result;
                            self.hasDatas = true;
                        } else {
                            self.hasDatas = false;
                        }
                        self.dataReady = true;
                    });
                },
                //添加学生干部
                addXsgb: function() {
                    //新增状态再点之间返回
                    if (this.isAdd) {
                        return;
                    }
                    this.formValue = {};
                    this.isAdd = true;
                },
                //取消学生干部
                cancelAdd: function() {
                    this.isAdd = false;
                    this.formValue = {};
                },
                //保存新增学生干部
                saveAdd: function() {
                    if (!this.$refs.formAdd.validate()) {
                        return false;
                    }
                    if (this.formValue.RZKSSJ && this.formValue.RZJSSJ && this.formValue.RZKSSJ > this.formValue.RZJSSJ) {
                        mintUI.Toast({
                            message: '任职开始时间不能大于任职结束时间！',
                            position: 'middle',
                            duration: 2000
                        });
                        return false;
                    }
                    this.formValue.XSBH = this.xsbh;
                    var self = this;
                    //新增
                    MOB_UTIL.doActionExecute({
                        url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_XSGB_ADD.do",
                        params: self.formValue,
                        name: "T_XG_XSGB_ADD"
                    }).done(function(result) {
                        mintUI.Toast({
                            message: '保存成功',
                            iconClass: 'iconfont mint-icon-i icon-chenggong'
                        });
                        //修改vue数组
                        // self.formValue.formName = "form" + self.xsgbArray.length;
                        // self.formValue.formRead = true;
                        // self.xsgbArray.push(self.formValue);
                        //获取学生干部记录
                        self.getXsgbRecords();
                        self.isEdit = false;
                        self.isAdd = false;
                        // self.hasDatas = true;
                        //清空
                        self.formValue = {};
                    });

                },
                //删除学生干部
                deleteXsgb: function(xsgb) {
                    var self = this;
                    var wid = xsgb.WID;
                    var param = {};
                    param['WID'] = wid;
                    mintUI.MessageBox.confirm('确定要删除该记录吗?', '提示').then(function() {
                        //删除
                        MOB_UTIL.doActionExecute({
                            url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_XSGB_DELETE.do",
                            params: param,
                            name: "T_XG_XSGB_DELETE"
                        }).done(function(result) {
                            //vue数组删除
                            // self.xsgbArray.splice(index, 1);
                            //获取学生干部记录
                            self.getXsgbRecords();
                            mintUI.Toast({
                                message: '删除成功',
                                iconClass: 'iconfont mint-icon-i icon-chenggong'
                            });
                        });
                    }, function() {});

                },
                //编辑学生干部
                editXsgb: function(index) {
                    //编辑状态再点之间返回
                    if (this.isEdit || this.isAdd) {
                        return;
                    }
                    this.isEdit = true;
                    this.oldEditFormValue = this.copyObj(this.xsgbArray[index]);
                    this.xsgbArray[index].formRead = false;
                },
                //取消编辑
                cancelEdit: function(index) {
                    this.isEdit = false;
                    this.xsgbArray[index].formRead = true;
                    this.xsgbArray[index] = this.oldEditFormValue;
                },
                //保存编辑
                saveEdit: function(index) {
                    var currentForm = 'form' + index;
                    if (!this.$refs[currentForm][0].validate()) {
                        return false;
                    }
                    var currentFormValue = this.xsgbArray[index];
                    if (currentFormValue.RZKSSJ && currentFormValue.RZJSSJ && currentFormValue.RZKSSJ > currentFormValue.RZJSSJ) {
                        mintUI.Toast({
                            message: '任职开始时间不能大于任职结束时间！',
                            position: 'middle',
                            duration: 2000
                        });
                        return false;
                    }
                    //修改保存
                    var self = this;
                    MOB_UTIL.doActionExecute({
                        url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_XSGB_SAVE.do",
                        params: currentFormValue,
                        name: "T_XG_XSGB_SAVE"
                    }).done(function(result) {
                        mintUI.Toast({
                            message: '保存成功',
                            iconClass: 'iconfont mint-icon-i icon-chenggong'
                        });
                        self.isEdit = false;
                        self.isAdd = false;
                        self.getXsgbRecords();
                        //self.xsgbArray[index].formRead = true;
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
                    retParam.FZDM = 'XSGBFZ';
                    this.$emit('save', retParam);
                    this.$router.go(-1);
                }
            }
        };
        return page;
    };

});