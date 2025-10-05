package com.baeldung.lju.suites;

import org.junit.platform.suite.api.*;

@Suite
@SelectPackages("com.baeldung.lju")
@ExcludePackages("com.baeldung.lju.suites")
@IncludeTags("fast")
class FastTests {
}