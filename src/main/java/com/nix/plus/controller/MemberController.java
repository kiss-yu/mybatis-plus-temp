package com.nix.plus.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nix.plus.common.Result;
import com.nix.plus.model.MemberModel;
import com.nix.plus.service.member.IMemberService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author by keray
 * date:2019/10/7 13:38
 */
@RestController
@RequestMapping("/api/member")
public class MemberController extends BaseController {

    @Resource(name = "memberService")
    private IMemberService memberService;

    @ResponseBody
    @GetMapping("/list")
    public Result list(
            @ModelAttribute Page<MemberModel> page
    ) {
        return Result.of(() -> memberService.page(page))
                .failFlat(this::failFlat).logFail();
    }

    @ResponseBody
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public Result add(
            @ModelAttribute MemberModel memberModel
    ) {
        return Result.of(() -> memberService.insert(memberModel)).failFlat(this::failFlat).logFail().throwE();
    }


    @ResponseBody
    @PutMapping("/edit")
    @Transactional(rollbackFor = Exception.class)
    public Result edit(@ModelAttribute MemberModel memberModel) {
        return Result.of(() -> memberService.update(memberModel)).failFlat(this::failFlat).logFail().throwE();
    }

    @ResponseBody
    @DeleteMapping("/delete")
    public Result delete(@RequestParam("ids") List<String> ids) {
        return Result.of(() -> memberService.delete(ids))
                .failFlat(this::failFlat).logFail();
    }
}
