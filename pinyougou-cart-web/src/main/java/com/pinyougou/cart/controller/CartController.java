package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.entity.Result;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
     * 从cookie中获取购物车
     *
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //获取购物车字符串
        String cartList = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        //判断 读取本地购物车
        if (cartList == null || "".equals(cartList)) {
            cartList = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartList, Cart.class);
        if (username.equals("anonymousUser")) {//未登录

            return cartList_cookie;

        }else {//已经登录了
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(username);

            if (cartList_cookie.size()>0){ //如果未登录cookie中存在购物车
                //合并购物车
                cartListFromRedis = cartService.mergeCartList(cartList_cookie, cartListFromRedis);
                //清除cookie数据
                CookieUtil.deleteCookie(request,response,"cartList");
                //存入redis
                cartService.saveCartListToRedis(username,cartListFromRedis);
            }
            return cartListFromRedis;
        }


    }

    /**
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录名:"+username);
        try {
            //从cookie中获取购物车cartList_cookie
            List<Cart> cartList = findCartList();
            //获取新的购物车
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if (username.equals("anonymousUser")){
                //存入cookie
                CookieUtil.setCookie(request, response, "cartList",
                        JSON.toJSONString(cartList), 3600 * 24, "utf-8");
            }else {//如果已经登录存入redis中
                cartService.saveCartListToRedis(username,cartList);
            }

            return new Result(true, "加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "加入购物车失败");
        }
    }


}
