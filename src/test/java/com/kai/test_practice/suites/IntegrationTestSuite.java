package com.kai.test_practice.suites;

//@Suite
//@SelectPackages("com.kai.test_practice.services") // 指定測試的 package
//@IncludeTags("integration") // 只包含帶有 @Tag("integration") 的測試
//public class IntegrationTestSuite {
//}


import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;

@IncludeTags("integration")
@SelectPackages("com.kai.test_practice.services")
public class IntegrationTestSuite {
}
