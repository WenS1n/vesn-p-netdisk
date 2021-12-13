package cn.vesns.netdisk.pojo;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Id;
import javax.persistence.Table;

@TableName("permission")
@Table(name = "permission")
public class Permission {

  @Id
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private long permissionId;
  private String available;
  private String name;
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private long parentId;
  private String parentIds;
  private String permission;
  private String resourceType;
  private String url;


  public long getPermissionId() {
    return permissionId;
  }

  public void setPermissionId(long permissionId) {
    this.permissionId = permissionId;
  }


  public String getAvailable() {
    return available;
  }

  public void setAvailable(String available) {
    this.available = available;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public long getParentId() {
    return parentId;
  }

  public void setParentId(long parentId) {
    this.parentId = parentId;
  }


  public String getParentIds() {
    return parentIds;
  }

  public void setParentIds(String parentIds) {
    this.parentIds = parentIds;
  }


  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }


  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }


  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}
