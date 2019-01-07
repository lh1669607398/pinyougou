app.service("CartService", function ($http) {
    this.findCartList = function () {
        $http.get("../cart/findCartList.do");
    }
    this.addGoodsToCartList = function (itemId, num) {
        $http.get("../cart/findCartList.do?itemId=" + itemId + "&num=" + num);
    }

    //求合计
    this.sum = function (cartList) {
        var totalValue = {totalNum: 0, totalMoney: 0.00};
        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) { //购物车明细
                totalValue.totalNum += cart.orderItemList[i].num;  //商品明细数量
                totalValue.totalMoney += orderItem.totalFee;  //商品明细总价
            }
        }
        return totalValue;
    }

})