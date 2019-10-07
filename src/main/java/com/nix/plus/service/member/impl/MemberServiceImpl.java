package com.nix.plus.service.member.impl;

import com.nix.plus.mapper.BaseMapper;
import com.nix.plus.mapper.MemberMapper;
import com.nix.plus.model.MemberModel;
import com.nix.plus.service.BaseServiceImpl;
import com.nix.plus.service.member.IMemberService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author by keray
 * date:2019/10/7 13:33
 */
@Service("memberService")
public class MemberServiceImpl extends BaseServiceImpl<MemberModel> implements IMemberService {

    @Resource
    private MemberMapper memberMapper;

    @Override
    public BaseMapper<MemberModel> getMapper() {
        return memberMapper;
    }
}
