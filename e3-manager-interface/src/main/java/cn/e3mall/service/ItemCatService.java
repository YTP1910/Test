package cn.e3mall.service;

import java.util.List;

import cn.common.pojo.EasyUITreeNode;

public interface ItemCatService {
	List<EasyUITreeNode> getCatList(long parentId);
}
