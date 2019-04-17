define(function() {
    var publicUtils = {
        /**
         * 获取基本信息分组模型是否可维护
         */
        getJBXXFZModelSFKWH: function(fzdm, zldm) {
            //默认不能维护
            var dfd = $.Deferred();
            var flag = false;
            var queryParam = {};
            queryParam['ZLDM'] = zldm;
            queryParam['FZDM'] = fzdm;
            MOB_UTIL.doActionQuery({
                url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_FZZD_SZ_QUERY.do",
                params: queryParam,
                name: "T_XG_FZZD_SZ_QUERY"
            }).done(function(result) {
                if (result && result.length > 0) {
                    for (var i = 0; i < result.length; i++) {
                        if (result[i].SFKG == '1') {
                            flag = true;
                        }
                    }
                }
                dfd.resolve(flag);
            });
            return dfd;
        },
        /**
         * 获取分组模型是否可维护
         */
        getFZModelSFKWH: function(fzdm, zldm) {
            //默认不能维护
            var dfd = $.Deferred();
            var flag = false;
            var queryParam = {};
            queryParam['ZLDM'] = zldm;
            queryParam['FZDM'] = fzdm;
            MOB_UTIL.doActionQuery({
                url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_FZ_SZ_QUERY.do",
                params: queryParam,
                name: "T_XG_FZ_SZ_QUERY"
            }).done(function(result) {
                if (result && result.length > 0) {
                    for (var i = 0; i < result.length; i++) {
                        if (result[i].FZ_SJWH == '1') {
                            flag = true;
                        }
                    }
                }
                dfd.resolve(flag);
            });
            return dfd;
        },
        //生成wid
        generateWId: function() {
            var dfd = $.Deferred();
            MOB_UTIL.doActionQuery({
                url: SWROOT_PATH + "/sqszpubjs/apply/zdscwid.do",
                params: {},
                name: "zdscwid"
            }).done(function(result) {
                dfd.resolve(result[0].WID);
            });
            return dfd;
        },
        /**
         * 获取分组模型信息
         */
        getFZModel: function(actionName, fzdm, zldm) {
            var self = this;
            var dfd = $.Deferred();
            var formModel = WIS_EMAP_SERV.getModel(SWROOT_PATH + "/sqszpubjs/apply.do", actionName, "form");
            for (var j = 0; j < formModel.length; j++) {
                //默认隐藏
                formModel[j].hidden = true;
            }
            var queryParam = {};
            queryParam['ZLDM'] = zldm;
            queryParam['FZDM'] = fzdm;
            MOB_UTIL.doActionQuery({
                    url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_FZZD_SZ_QUERY.do",
                    params: queryParam,
                    name: "T_XG_FZZD_SZ_QUERY"
                })
                .done(function(result) {
                    //格式化表单模型
                    self.formatModel(result, formModel);
                    //申请陈述和附件只要勾选即展示
                    if (fzdm == "SQCSFZ" || fzdm == "FJFZ") {
                        MOB_UTIL.doActionQuery({
                                url: SWROOT_PATH + "/sqszpubjs/apply/T_XG_FZ_SZ_QUERY.do",
                                params: queryParam,
                                name: "T_XG_FZ_SZ_QUERY"
                            })
                            .done(function(result) {
                                if (result && result.length > 0 && result[0].SFSY == "1") {
                                    for (var j = 0; j < formModel.length; j++) {
                                        formModel[j].hidden = false;
                                    }
                                    dfd.resolve(formModel);
                                }
                            });
                    } else {
                        dfd.resolve(formModel);
                    }
                });
            return dfd;
        },
        //格式化表单模型
        formatModel: function(result, formModel) {
            if (result && result.length > 0) {
                for (var i = 0; i < result.length; i++) {
                    for (var j = 0; j < formModel.length; j++) {
                        if (result[i].ZDDM == formModel[j].name) {
                            if (result[i].ZDZ == '1' || result[i].ZDZS || result[i].ZXZS) {
                                formModel[j].hidden = false;
                            }
                        }
                    }
                }
            }
        },
        getDictionaryOfFbxn: function() {
            return SWROOT_PATH + '/MobileCommon/getDictionaryOfFbxn.do';
        }
    };
    return publicUtils;
});