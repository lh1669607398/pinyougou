app.controller('searchController', function ($scope, searchService,$location) {
    //定义搜索对象结构
    $scope.searchMap = {
        'keywords': '',    // 搜索关键字
        'category': '',    //  商品分类
        'brand': '',       // 品牌
        'spec': {},        // 规格和规格选项
        'price': '',       // 价格区间
        'pageNo': 1,    // 当前页码
        'pageSize': 40,   // 展示条数
        'sortField': '',  //排序字段
        'sort': ''     //排序方式顺序
    };

    //搜索
    $scope.search = function () {
        //将传过来的字符串装换的数字
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        //调用方法搜索关键字
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;//搜索返回的结果
                buildPageLabe();//构建分页

            }
        );
    }

    //构建分页
    buildPageLabe = function () {
        $scope.pageLabe = [];//定义一个分页页码数组
        var firstPage = 1;//开始页码;
        var lastPage = $scope.resultMap.totalPages;//总页码数
        //省略号
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后面有点
        if ($scope.resultMap.totalPages > 5) {  //总页码数大于5
            if ($scope.searchMap.pageNo <= 3) { //当前页码数小于等于3
                lastPage = 5;
                $scope.firstDot = false;//前面无点
            } else if ($scope.searchMap.pageNo >= $scope.resultMap.totalPages - 2) { //当前页码数大于等于总页数-2
                firstPage = $scope.searchMap.pageNo - 4;
                $scope.lastDot = false;//后面无点
            } else {  //总页数在折中的
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;

            }
        } else {
            $scope.firstDot = false;//前面无点
            $scope.lastDot = false;//后面无点
        }
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabe.push(i);
        }
    };


    //添加搜索项,改变searchMap的值
    $scope.addSearchItem = function (key, value) {
        //如果点击是分类和品牌
        if (key == 'category' || key == 'brand' || key == 'price') {
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
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = "";
        } else {
            //如果点击的规格
            delete $scope.searchMap.spec[key];
        }
        $scope.search();//查询
    }

    //根据页码查询
    $scope.queryByPage = function (pageNo) {
        //页码验证
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return
        }
        $scope.searchMap.pageNo = pageNo;  //确保是当前页码
        $scope.search();//从新查询
    }
    //页码样式不可用
    $scope.isTopPage = function () { //判断当前页是否是第一页
        if ($scope.searchMap.pageNo == 1) {
            return true
        } else {
            return false
        }
    }
    //判断当前页是否未最后一页
    $scope.isEndPage = function () {
        var max = $scope.resultMap.totalPages;
        if ($scope.searchMap.pageNo == max) {
            return true;
        } else {
            return false;
        }
    }

    //设置排序规则
    $scope.sortSearch=function (sortField,sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();//重新查询
    }
    //隐藏品牌列表
    $scope.keywordsIsBrand=function () {
        for (var i = 0;i<$scope.resultMap.brandList;i++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }
    //接受参数并查询
    $scope.loadkeywords=function () {
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search();//查询
    }



});
