define(function(require, exports, module) {
    var tpl = require('text!publicVueComponent/home/home.html');

    return function() {
        var page = {
            template: tpl,
            data: function() {
                return {
                    homeSelectedTab: "",
                    pageList: window.REQUIRE_MODULES_ARR.indexPages
                };
            },
            mounted: function() {
                this.homeSelectedTab = this.$route.meta.index;
            },
            watch: {
                '$route': function(to, from) {
                    this.homeSelectedTab = this.$route.meta.index;
                }
            },
            methods: {
                changePage: function(page) {
                    this.$router.replace({
                        name: page.name
                    });
                }
            }
        };
        return page;
    };

});