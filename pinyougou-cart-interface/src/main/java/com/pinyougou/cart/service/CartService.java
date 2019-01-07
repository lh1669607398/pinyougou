package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

//操作购物车接口
public interface CartService {
    //添加购物车
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    //从redis中查询购物车
    public List<Cart> findCartListFromRedis(String userName);

    //将购物车存入redis中
    public void saveCartListToRedis(String userName, List<Cart> cartList);

    //合并购物车
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);
}