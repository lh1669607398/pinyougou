app.controller('searchController', function ($scope, searchService) {
    //定义搜索对象结构    搜索关键字       商品分类      品牌        规格和规格选项
    $scope.searchMap = {'keywords': '', 'category': '', 'brand': '', 'spec': {}};
    //搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;//搜索返回的结果
            }
        );
    }
    //添加搜索项,改变searchMap的值
    $scope.addSearchItem = function (key, value) {
        //如果点击是分类和品牌
        if (key == 'category' || key == 'brand') {
            $scope.searchMap[key] = value;
        } else {
            //如果点击的规格
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//查询
    }
    //添加搜索项,删除searchMap的值
    $scope.delteSearchItem = function (key) {
        //如果点击是分类和品牌
        if (key == 'category' || key == 'brand') {
            $scope.searchMap[key] = "";
        } else {
            //如果点击的规格
            delete $scope.searchMap.spec[key];
        }
        $scope.search();//查询
    }

});
