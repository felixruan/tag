package org.etocrm.tagManager.model.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class YoungorDataVO {
    @ApiModelProperty(value = "会员id")
    private String memberId;

    @ApiModelProperty(value = "会员电话")
    private String phone;

    @ApiModelProperty(value = "注册时间")
    private String registerTime;

    @ApiModelProperty(value = "会员卡号")
    @NotBlank(message = "会员卡号不能为空")
    private String cardno;

    @ApiModelProperty(value = "真实姓名")
    private String realname;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "年龄段")
    private String age;

    @ApiModelProperty(value = "出生日期")
    private String birthDate;

    @ApiModelProperty(value = "出生月份")
    private String birthMonth;

    @ApiModelProperty(value = "生肖")
    private String chineseZodiac;

    @ApiModelProperty(value = "星座")
    private String constellation;

    @ApiModelProperty(value = "所属省份城市")
    private String provinceCity;

    @ApiModelProperty(value = "所属门店编码")
    private String storeCode;

    @ApiModelProperty(value = "所属门店名称")
    private String storeName;

    @ApiModelProperty(value = "所属雅戈尔片区编码")
    private String pianquCode;

    @ApiModelProperty(value = "所属雅戈尔片区名称")
    private String pianquName;

    @ApiModelProperty(value = "所属雅戈尔区域编码")
    private String areaCode;

    @ApiModelProperty(value = "所属雅戈尔区域名称")
    private String areaName;

    @ApiModelProperty(value = "所属雅戈尔营销公司编码")
    private String yxgsCode;

    @ApiModelProperty(value = "所属雅戈尔营销公司名称")
    private String yxgsName;

    @ApiModelProperty(value = "入会时长年")
    private String initiationTime;

    @ApiModelProperty(value = "会员等级")
    private String memberLevel;

    @ApiModelProperty(value = "会员生命周期")
    private String memberLifeCycle;

    @ApiModelProperty(value = "当前可用积分")
    private String currentPoint;

    @ApiModelProperty(value = "累计消费区间")
    private String consumSum;

    @ApiModelProperty(value = "当年购买次数")
    private String consumptionNumYtd;

    @ApiModelProperty(value = "累计购买次数")
    private String consumptionNum;

    @ApiModelProperty(value = "上月有无消费")
    private String consumptionLastMonth;

    @ApiModelProperty(value = "上季度有无消费")
    private String consumptionLastQuarter;

    @ApiModelProperty(value = "上一个半年有无消费")
    private String consumptionFirstHalfYear;

    @ApiModelProperty(value = "去年有无消费")
    private String consumptionLastYear;

    @ApiModelProperty(value = "购买过的品牌")
    private String consumBrand;

    @ApiModelProperty(value = "品牌偏好")
    private String brandPreferName;

    @ApiModelProperty(value = "大类偏好")
    private String daleiPreferName;

    @ApiModelProperty(value = "风格偏好")
    private String prodStylePreferName;

    @ApiModelProperty(value = "季节偏好")
    private String seasonPreferName;

    @ApiModelProperty(value = "颜色偏好")
    private String colorPreferName;

    @ApiModelProperty(value = "系列偏好")
    private String seriesPreferName;

    @ApiModelProperty(value = "版型偏好")
    private String banxingPreferName;

    @ApiModelProperty(value = "消费区间偏好")
    private String consumRangePrefer;

    @ApiModelProperty(value = "折扣区间偏好")
    private String discountRangePrefer;

    @ApiModelProperty(value = "近30天消费日期偏好")
    private String consumDayClassPreferLm;

    @ApiModelProperty(value = "近90天消费日期偏好")
    private String consumDayClassPreferLq;

    @ApiModelProperty(value = "消费时间段区间偏好")
    private String consumtimeRangePrefer;

    @ApiModelProperty(value = "消费渠道偏好")
    private String consumChannelPreferName;

    @ApiModelProperty(value = "线下支付方式偏好")
    private String payModePreferNameOffline;
}
