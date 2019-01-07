package com.pinyougou.cart.service.impl;

import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//购物车实现类
public class CartServiceIml implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    //添加购物车
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据skuId查询商品明细sku对象
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        //2.根据商品明细sku获取商家对象
        String sellerId = tbItem.getSellerId();//获取商家名称
        //3.根据商家ID获查询物车列表中的购物车对象
        Cart cart = searchCartBySellerId(cartList, sellerId);
        //4.如果购物车列表中不包含购物车对象
        if (cart==null){
            //4.1 创建购物车对象
            cart=new Cart();
            //4.2 将商品明细sku添加购物车对象列表中
            TbOrderItem orderItem = createOrderItem(itemId, num);
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());
            List<TbOrderItem> list=new ArrayList<>();
            list.add(orderItem);
            cart.setOrderItems(list);
            //4.3 将购物车对象添加进购物车列表中
            cartList.add(cart);
        }else {
            //5 如果购物车列表中包含购物车对象.
            List<TbOrderItem> orderItemList = cart.getOrderItems();
            //5.1判断该商品明细是否在购物车对象中存在
            TbOrderItem tbOrderItem = searchOrderItemByItemId(orderItemList, itemId);
            if (tbOrderItem == null) {
                //5.2 如果不存在,则创建商品明细
                tbOrderItem = createOrderItem(itemId, num);
                //5.3添加进购物车对象 添加进购物车列表中
                cart.getOrderItems().add(tbOrderItem);
            }else {
                //如果存在,则在增加数量
                tbOrderItem.setNum(tbOrderItem.getNum()+num);
                //增加金额
                tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getNum()*tbOrderItem.getPrice().doubleValue()));
                //如果商品明细不存在,则移除
                if (tbOrderItem.getNum()<=0){
                    orderItemList.remove(tbOrderItem);
                }
                //如果购物车对象不存在,则移除
                if (cart.getOrderItems().size()==0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    /**
     * 根据商家ID获查询物车列表中的购物车对象
     * @param cartList
     * @param sellerId
     * @return
     */
    public Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        for (Cart cart : cartList) {
            if (sellerId.equals(cart.getSellerId())){
                return  cart;
            }
        }
        return null;
    }

    /**
     *
     * @param itemId
     * @param num
     * @return
     */
    public TbOrderItem createOrderItem(Long itemId, Integer num){
        if (num<=0){
           throw new RuntimeException("数量非法");
        }
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        TbOrderItem tbOrderItem= new TbOrderItem();
            tbOrderItem.setItemId(itemId);
            tbOrderItem.setGoodsId(tbItem.getGoodsId());
            tbOrderItem.setId(tbItem.getId());
            tbOrderItem.setNum(num);
            tbOrderItem.setPicPath(tbItem.getImage());
            tbOrderItem.setTitle(tbItem.getTitle());
            tbOrderItem.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue()*num));
            return tbOrderItem;
    }

    /**
     * 根据商品明细sku Id查询
     * @param orderItemList
     * @param itemId
     * @return
     */
    public TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
            if (itemId.equals(orderItem.getItemId())){
                return orderItem;
            }
        }
        return null;
    }
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 从缓存中获取购物车
     * @param userName
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String userName) {
        System.out.println("从redis中获取购物车");
      /*  String cartList = (String) redisTemplate.boundHashOps("cartList").get(userName);
        if (cartList==null||"".equals(cartList)){
            cartList="[]";
        }
        List<Cart> cartList_redis = JSON.parseArray(cartList, Cart.class);*/
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userName);
        if (cartList==null){
            cartList=new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 存入购物车到缓存中
     * @param userName
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String userName, List<Cart> cartList) {
        //想缓存中存入购物车
        redisTemplate.boundHashOps("cartList").put(userName,cartList);
    }

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        System.out.println("合并购物车");
        for (Cart cart : cartList2) {
            for (TbOrderItem orderItem : cart.getOrderItems()) {
               cartList1 = addGoodsToCartList(cartList1, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList1;
    }
}
