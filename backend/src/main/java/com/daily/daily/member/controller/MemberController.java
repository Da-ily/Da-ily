package com.daily.daily.member.controller;

import com.daily.daily.common.dto.CommonResponseDTO;
import com.daily.daily.member.dto.DuplicateResultDTO;
import com.daily.daily.member.dto.EmailDTO;
import com.daily.daily.member.dto.EmailVerifyDTO;
import com.daily.daily.member.dto.JoinDTO;
import com.daily.daily.member.dto.MemberInfoDTO;
import com.daily.daily.member.dto.NicknameDTO;
import com.daily.daily.member.dto.PasswordRecoverDTO;
import com.daily.daily.member.dto.PasswordTokenDTO;
import com.daily.daily.member.dto.PasswordUpdateDTO;
import com.daily.daily.member.service.MemberEmailService;
import com.daily.daily.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final MemberEmailService memberEmailService;

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberInfoDTO join(@RequestBody @Valid JoinDTO joinDTO) {
        return memberService.join(joinDTO);
    }

    @Secured(value = "ROLE_MEMBER")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public MemberInfoDTO getMemberByAccessToken(@AuthenticationPrincipal Long id) {
        return memberService.findById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/check-username")
    public DuplicateResultDTO checkDuplicatedUsername(@RequestParam("username") String username) {
        System.out.println(username);
        if (memberService.existsByUsername(username)) {
            return new DuplicateResultDTO(true);
        }

        return new DuplicateResultDTO(false);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/check-nickname")
    public DuplicateResultDTO checkDuplicatedNickname(@RequestParam("nickname") String nickname) {
        if (memberService.existsByNickname(nickname)) {
            return new DuplicateResultDTO(true);
        }

        return new DuplicateResultDTO(false);
    }

    @Secured(value = "ROLE_MEMBER")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/nickname")
    public MemberInfoDTO updateNickname(
            @RequestBody @Valid NicknameDTO nicknameDTO,
            @AuthenticationPrincipal Long id
    ) {
        return memberService.updateNickname(id, nicknameDTO.getNickname());
    }

    @Secured(value = "ROLE_MEMBER")
    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponseDTO updatePassword(@RequestBody @Valid PasswordUpdateDTO passwordUpdateDTO, @AuthenticationPrincipal Long id) {
        memberService.updatePassword(passwordUpdateDTO, id);
        return new CommonResponseDTO(true, HttpStatus.OK.value());
    }

    @Secured(value = "ROLE_MEMBER")
    @PostMapping("/email-verification/request")
    public ResponseEntity<CommonResponseDTO> sendCertificationNumber(@RequestBody @Valid EmailDTO emailDTO) {
        memberEmailService.sendCertificationNumber(emailDTO.getEmail());
        return new ResponseEntity<>(new CommonResponseDTO(true, HttpStatus.OK.value()), HttpStatus.OK);
    }

    @Secured(value = "ROLE_MEMBER")
    @PostMapping("/email-verification/confirm")
    public ResponseEntity<CommonResponseDTO> verifyEmailAndRegister(
            @RequestBody @Valid EmailVerifyDTO emailVerifyDTO,
            @AuthenticationPrincipal Long id
    ) {
        memberEmailService.verifyEmailAndRegister(id, emailVerifyDTO);
        return new ResponseEntity<>(new CommonResponseDTO(true, HttpStatus.OK.value()), HttpStatus.OK);
    }

    @PostMapping("/recover-username")
    public ResponseEntity<CommonResponseDTO> recoverUsername(@RequestBody @Valid EmailDTO emailDTO) {
        memberEmailService.sendUsername(emailDTO.getEmail());
        return new ResponseEntity<>(new CommonResponseDTO(true, HttpStatus.OK.value()), HttpStatus.OK);
    }

    @PostMapping("/recover-password")
    public ResponseEntity<CommonResponseDTO> recoverPassword(@RequestBody @Valid PasswordRecoverDTO passwordRecoverDTO) {
        memberEmailService.recoverPassword(passwordRecoverDTO.getUsername(), passwordRecoverDTO.getEmail());
        return new ResponseEntity<>(new CommonResponseDTO(true, HttpStatus.OK.value()), HttpStatus.OK);
    }

    @PatchMapping("/recover-password")
    public ResponseEntity<CommonResponseDTO> updatePasswordByResetToken(@RequestBody @Valid PasswordTokenDTO passwordTokenDTO) {
        memberService.updatePasswordByResetToken(passwordTokenDTO.getPasswordResetToken(), passwordTokenDTO.getPassword());
        return new ResponseEntity<>(new CommonResponseDTO(true, HttpStatus.OK.value()), HttpStatus.OK);
    }
}
