package com.example.demo.controller;

import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.constant.MessageConst;
import com.example.demo.constant.UrlConst;
import com.example.demo.constant.ViewNameConst;
import com.example.demo.form.LoginForm;
import com.example.demo.service.LoginService;
import com.example.demo.util.AppUtil;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
//@RequestMapping("/login")
public class LoginController {
	
	/*ログイン画面 Service*/
	private final LoginService service;
	
	/*PasswordEncoder*/
	//PasswordEncoderの中にBCryptPasswordEncoderが入る
	private final PasswordEncoder passwordEncoder;
	
	/*メッセージソース*/
	private final MessageSource messagesource;
	
	/*セッション情報*/
      private final HttpSession session;	
	@GetMapping(UrlConst.LOGIN)
	public String view(Model model,LoginForm form) {
		
		
		return ViewNameConst.LOGIN;
	}
	
    /*ログインエラー画面表示*/
	@GetMapping(value= UrlConst.LOGIN,params="error")
	public String viewWithError(Model model,LoginForm form) {
		var errorInfo=(Exception)session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		model.addAttribute("errorMsg", errorInfo.getMessage());
		return ViewNameConst.LOGIN;
	}
	
	
	@PostMapping(UrlConst.LOGIN) // /loginのURLで受け取る
	public String login(Model model,LoginForm form) {//Modelクラスを使えば画面にControllerからの情報を渡せる
		var userInfo=service.searchUserById(form.getLoginId()); //serviceクラスのsearchメソッドを使う
		var isCorrectUserAuth=userInfo.isPresent()  //userInfoが取れたらboleanの結果：userInfoになんかデータが取れたら
				&& passwordEncoder.matches(form.getPassword(), userInfo.get().getPassword());//第一引数：入力されたPW、第二引数:DBのPW
		if(isCorrectUserAuth) { //一致したらture 何もデータがなかったらfalse 取れてもPWが一致しなかったらfalse
			return "redirect:/menu";  //ログイン成功時
		}else {//ログイン失敗した時エラーメッセージ出す
			var errorMsg=AppUtil.getMessage(messagesource, MessageConst.LOGIN_WRONG_INPUT);
			//modelにエラーメッセージを格納する
			model.addAttribute("errorMsg", errorMsg);
			return ViewNameConst.LOGIN;
		}
	}
	
	
}
