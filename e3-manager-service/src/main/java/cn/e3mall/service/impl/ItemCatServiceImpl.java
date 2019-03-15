package cn.e3mall.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.common.pojo.EasyUITreeNode;
import cn.e3mall.mapper.TbItemCatMapper;
import cn.e3mall.pojo.TbItemCat;
import cn.e3mall.pojo.TbItemCatExample;
import cn.e3mall.pojo.TbItemCatExample.Criteria;
import cn.e3mall.service.ItemCatService;

@Service
public class ItemCatServiceImpl implements ItemCatService {
	@Autowired
	private TbItemCatMapper tbItemCatMapper;
	@Override
	public List<EasyUITreeNode> getCatList(long parentId) {
		
		TbItemCatExample example=new TbItemCatExample();
		//设置查询条件
		Criteria create = example.createCriteria();
		create.andParentIdEqualTo(parentId);
		List<TbItemCat> tbItemCat = tbItemCatMapper.selectByExample(example);
		//转换成EasyUITreeNode 列表
		List<EasyUITreeNode> resultList = new ArrayList<>();
		for (TbItemCat itemcat : tbItemCat) {
			EasyUITreeNode node=new EasyUITreeNode();
			node.setId(itemcat.getId());
			node.setText(itemcat.getName());
			node.setState(itemcat.getIsParent()?"closed":"open");
			//添加列表
			resultList.add(node);
		}
		return resultList;
	}

}
