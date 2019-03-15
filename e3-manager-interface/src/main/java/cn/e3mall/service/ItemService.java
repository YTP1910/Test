package cn.e3mall.service;


import cn.common.pojo.EasyUIDataGridResult;
import cn.common.uilt.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;

public interface ItemService {

	TbItem getItemById(long itemId);
	
	TbItemDesc getItemDescById(long itemId);
	
	EasyUIDataGridResult newTbItemList(int page,int rows);
	
	public E3Result saveTbItem(TbItem  item,String desc);

	E3Result updateTbItem(TbItem item, String desc);

	E3Result deleteTbItem(long id);

	E3Result getItem(long itemId);

	E3Result getDesc(long itemId);

	E3Result putawayState(long itemId);

	E3Result soldState(long itemId);
}
