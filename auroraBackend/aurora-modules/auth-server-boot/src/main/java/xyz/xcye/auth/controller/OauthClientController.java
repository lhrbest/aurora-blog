package xyz.xcye.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.xcye.auth.constant.OauthJwtConstant;
import xyz.xcye.auth.pojo.OauthClientDetailsPojo;
import xyz.xcye.auth.service.OauthClientDetailsService;
import xyz.xcye.auth.vo.OauthClientDetailsVO;
import xyz.xcye.core.annotaion.FieldFilter;
import xyz.xcye.core.annotaion.business.SetCondition;
import xyz.xcye.core.annotaion.controller.ModifyOperation;
import xyz.xcye.core.annotaion.controller.SelectOperation;
import xyz.xcye.core.exception.user.UserException;
import xyz.xcye.core.valid.Insert;
import xyz.xcye.core.valid.Update;
import xyz.xcye.data.entity.Condition;
import xyz.xcye.data.entity.PageData;

import javax.validation.groups.Default;

/**
 * @author xcye
 * @description
 * @date 2022-12-19 22:02:13
 */

@RequestMapping("/auth/oauthClient")
@RestController
public class OauthClientController {
    @Autowired
    private OauthClientDetailsService oauthClientDetailsService;

    @ModifyOperation
    @Operation(summary = "根据clientId删除配置")
    @PostMapping("/physicalDeleteOauthClient")
    public int physicalDeleteOauthClient(@RequestBody OauthClientDetailsPojo pojo) {
        return oauthClientDetailsService.physicalDeleteOauthClient(pojo);
    }

    @FieldFilter(value = OauthClientDetailsVO.class, excludeFields = {"","",""},
            ignoreRole = {OauthJwtConstant.SUPER_ADMINISTRATOR_ROLE_NAME})
    @SelectOperation
    @Operation(summary = "根据条件查询")
    @PostMapping("/queryListOauthClientByCondition")
    @SetCondition(keyword = "username")
    public PageData<OauthClientDetailsVO> queryListOauthClientByCondition(@RequestBody Condition<Object> condition) {
        return oauthClientDetailsService.queryListOauthClientByCondition(condition);
    }

    @PostMapping("/insertOauthClient")
    @ModifyOperation
    @Operation(summary = "添加Oauth配置")
    public void insertUser(@Validated({Insert.class, Default.class}) @RequestBody OauthClientDetailsPojo pojo) throws UserException {
        oauthClientDetailsService.insertOauthClient(pojo);
    }

    @PostMapping("/updateOauthClient")
    @ModifyOperation
    @Operation(summary = "修改Oauth配置")
    public Integer updateUser(@Validated({Update.class, Default.class}) @RequestBody OauthClientDetailsPojo pojo) throws UserException {
        return oauthClientDetailsService.updateOauthClient(pojo);
    }

    @PostMapping("/queryOneOauthClientByClientId")
    @SelectOperation
    @Operation(summary = "通过uid查询用户信息")
    public OauthClientDetailsVO queryOauthClientByClientId(@RequestBody OauthClientDetailsPojo pojo) {
            return oauthClientDetailsService.queryOauthClientByClientId(pojo);
    }

}
