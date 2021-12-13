package cn.vesns.netdisk.pojo;/**
 * @version :JDK1.8
 * @date : 2021-11-03 20:32
 * @author : vip865047755@126.com
 * @File : TreeNode.java
 * @software: IntelliJ IDEA
 */

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: vesns vip865047755@126.com
 * @Title: TreeNode
 * @ProjectName: netdisk
 * @Description:
 * @date: 2021-11-03 20:32
 * 树节点
 */

@Data
public class TreeNode {

    /**
     * 节点id
     */
    private Long id;

    /**
     *节点名称
     */
    private String label;

    /**
     * 深度
     */
    private Long depth;

    /**
     *是否被关闭
     */
    private String state = "closed";

    private String filePath = "/";

    /**
     * 属性集合
     */
    private Map<String, String> attributes = new HashMap<>();

    /**
     * 子节点列表
     */
    private List<TreeNode> children = new ArrayList<>();

}
