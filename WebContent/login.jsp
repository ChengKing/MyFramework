<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<html xmlns:wb="http://open.weibo.com/wb">
<HEAD>
<TITLE>用户登录</TITLE>
<LINK href="<%=request.getContextPath()%>/css/Default.css" type=text/css
	rel=stylesheet>
<LINK href="<%=request.getContextPath()%>/css/xtree.css" type=text/css
	rel=stylesheet>
<LINK href="<%=request.getContextPath()%>/css/User_Login.css"
	type=text/css rel=stylesheet>
<META http-equiv=Content-Type content="text/html; charset=utf-8">
<meta name="keywords"
	content="weibo,javascript sdk,jssdk,open.weibo.com" />
<meta name="description" content="weibo jssdk是一个面向Javascript程序员的SDK。" />
<script
	src="http://tjs.sjs.sinajs.cn/open/api/js/wb.js?appkey=2554747276"
	type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
function login(o) {
    alert(o.screen_name);
}
 
function logout() {
    alert('logout');
}
</script>
</HEAD>
<BODY id=userlogin_body>
	<DIV></DIV>
	<FORM name="loginForm" method="POST" action="">
		<DIV id=user_login>
			<DL>
				<DD id=user_top>
					<UL>
						<LI class=user_top_l></LI>
						<LI class=user_top_c></LI>
						<LI class=user_top_r></LI>
					</UL>
				<DD id=user_main>
					<UL>
						<LI class=user_main_l></LI>
						<LI class=user_main_c>
							<DIV class=user_main_box>
								<UL>
									<LI class=user_main_text>用户名</LI>
									<LI class=user_main_input><INPUT class=TxtUserNameCssClass
										id=TxtUserName maxLength=20 name=TxtUserName></LI>
								</UL>
								<UL>
									<LI class=user_main_text>密 码</LI>
									<LI class=user_main_input><INPUT class=TxtPasswordCssClass
										id=TxtPassword type=password name=TxtPassword></LI>
								</UL>
								<UL>
									<LI class=user_main_text>验证码</LI>
									<LI class=user_main_input><input name="verifyCode"
										id="verifyCode" size="10" /></LI>
									<LI></LI>
								</UL>
								<UL>
									<li>其他方式：</li>
									<li><wb:login-button type="4,3" onlogin="login" onlogout="logout" ></wb:login-button>
									</li>
								</UL>

							</DIV>
						</LI>
						<LI class=user_main_r><INPUT class=IbtnEnterCssClass
							id=IbtnEnter
							style="BORDER-TOP-WIDTH: 0px; BORDER-LEFT-WIDTH: 0px; BORDER-BOTTOM-WIDTH: 0px; BORDER-RIGHT-WIDTH: 0px"
							onclick='javascript:WebForm_DoPostBackWithOptions(new WebForm_PostBackOptions("IbtnEnter", "", true, "", "", false, false))'
							type=image src="images/user_botton.gif" name=IbtnEnter></LI>
					</UL>
				<DD id=user_bottom>
					<UL>
						<LI class=user_bottom_l></LI>
						<LI class=user_bottom_c><SPAN style="MARGIN-TOP: 40px">Copyright
								© 2004 - 2013 chengking@monkey </SPAN></LI>
						<LI class=user_bottom_r></LI>
					</UL>
				</DD>
			</DL>
		</DIV>
		<SPAN id=ValrUserName style="DISPLAY: none; COLOR: red"></SPAN> <SPAN
			id=ValrPassword style="DISPLAY: none; COLOR: red"></SPAN> <SPAN
			id=ValrValidateCode style="DISPLAY: none; COLOR: red"></SPAN>
		<DIV id=ValidationSummary1 style="DISPLAY: none; COLOR: red"></DIV>
		<DIV></DIV>
	</FORM>
</BODY>
</HTML>
