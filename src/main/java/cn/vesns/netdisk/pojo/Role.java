package cn.vesns.netdisk.pojo;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "role")

@TableName("role")
public class Role {

  @Id
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private long roleId;

  private String available;

  private String description;

  private String role;


  public long getRoleId() {
    return roleId;
  }

  public void setRoleId(long roleId) {
    this.roleId = roleId;
  }


  public String getAvailable() {
    return available;
  }

  public void setAvailable(String available) {
    this.available = available;
  }


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

}
