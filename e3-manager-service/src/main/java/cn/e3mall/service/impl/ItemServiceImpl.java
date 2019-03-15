package cn.e3mall.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.jdbc.StringUtils;

import cn.common.jedis.JedisClient;
import cn.common.pojo.EasyUIDataGridResult;
import cn.common.uilt.E3Result;
import cn.common.uilt.IDUtils;
import cn.common.uilt.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemExample.Criteria;
import cn.e3mall.service.ItemService;

/**
 * 商品管理Service
 * <p>Title: ItemServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private JedisClient jedisClient;
	@Override
	public TbItem getItemById(long itemId) {
		try {
			String string = jedisClient.get("ITEM_INFO_PRE:" + itemId + ":BASE");
			if(!StringUtils.isNullOrEmpty(string)) {
				TbItem tbItem = JsonUtils.jsonToPojo(string, TbItem.class);
				return tbItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//根据主键查询
		//TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andIdEqualTo(itemId);
		//执行查询
		List<TbItem> list = itemMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			try {
				jedisClient.set("ITEM_INFO_PRE:" + itemId + ":BASE", JsonUtils.objectToJson(list.get(0)));
				//jedisClient.expire("ITEM_INFO_PRE:" + itemId + ":BASE", 30);
				return list.get(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public EasyUIDataGridResult newTbItemList(int page,int rows) {
		//设置页数
		PageHelper.startPage(page, rows);
		
		TbItemExample example=new TbItemExample();
		List<TbItem> items=itemMapper.selectByExample(example);
		PageInfo<TbItem> pageInfo=new PageInfo<>(items);
		
		//创建发回对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(items);
		result.setTotal(pageInfo.getTotal());
		return result;
	}

	@Override
	public E3Result saveTbItem(TbItem item,String desc) {
		//生成id
		long itemId = IDUtils.genItemId();
		item.setId(itemId);
		//商品状态
		item.setStatus((byte) 1);
		Date date=new Date();
		item.setCreated(date);
		item.setUpdated(date);
		//执行添加商品
		itemMapper.insert(item);
		//补全商品描述
		TbItemDesc itemDesc=new TbItemDesc();
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		itemDescMapper.insert(itemDesc);
		return E3Result.ok();
	}

	@Override
	public E3Result updateTbItem(TbItem item, String desc) {
		
		return null;
	}

	@Override
	public E3Result deleteTbItem(long id) {
			itemMapper.deleteByPrimaryKey(id);
			itemDescMapper.deleteByPrimaryKey(id);
			return E3Result.ok();
	}

	@Override
	public E3Result getItem(long itemId) {
		return E3Result.ok(itemMapper.selectByPrimaryKey(itemId));
	}

	@Override
	public E3Result getDesc(long itemId) {
		return  E3Result.ok(itemDescMapper.selectByPrimaryKey(itemId));
	}

	@Override
	public E3Result putawayState(long itemId) {
		TbItem item=new TbItem();
		item.setId(itemId);
		item.setStatus((byte)1);
		itemMapper.updateByPrimaryKeySelective(item);
		return E3Result.ok();
	}

	@Override
	public E3Result soldState(long itemId) {
		TbItem item=new TbItem();
		item.setId(itemId);
		item.setStatus((byte)2);
		itemMapper.updateByPrimaryKeySelective(item);
		return E3Result.ok();
	}

	@Override
	public TbItemDesc getItemDescById(long itemId) {
		try {
			String string = jedisClient.get("ITEM_INFO_PRE:" + itemId + ":DESC");
			if(!StringUtils.isNullOrEmpty(string)) {
				TbItemDesc jsonToPojo = JsonUtils.jsonToPojo(string, TbItemDesc.class);
				return jsonToPojo;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		try {
			jedisClient.set("ITEM_INFO_PRE:" + itemId + ":DESC",JsonUtils.objectToJson(tbItemDesc));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tbItemDesc;
	}
	
	

}
