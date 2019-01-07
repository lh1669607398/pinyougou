app.controller("cartController",function ($scop,cartService) {
    //查询购物车列表
    $scop.findCartList=function () {
        cartService.findCartList().success(
            function (response) {
                $scop.cartList=response;
                $scope.totalValue=cartService.sum($scope.cartList);//求合计数
            }
        )
    }
    //添加进购物车
    $scop.addGoodsToCartList=function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(
            function (response) {
                if (response.success){
                    $scop.findCartList();//刷新列表
                }else {
                    alert(response.message)  //给出提示信息
                }
            }
        )
    }



    
})