Link 1:
https://dev.automationtesting.in/shadow-dom
//This Element is inside single shadow DOM.
String cssSelectorForHost1 = "#shadow-root";
Thread.sleep(1000);
SearchContext shadow = driver.findElement(By.cssSelector("#shadow-root")).getShadowRoot();
Thread.sleep(1000);
shadow.findElement(By.cssSelector("#shadow-element"));


//This Element is inside 2 nested shadow DOM.
String cssSelectorForHost1 = "#shadow-root";
String cssSelectorForHost2 = "#inner-shadow-dom";
Thread.sleep(1000);
SearchContext shadow0 = driver.findElement(By.cssSelector("#shadow-root")).getShadowRoot();
Thread.sleep(1000);
SearchContext shadow1 = shadow0.findElement(By.cssSelector("#inner-shadow-dom")).getShadowRoot();
Thread.sleep(1000);
shadow1.findElement(By.cssSelector("#nested-shadow-element"));


//This Element is inside 3 nested shadow DOM.
String cssSelectorForHost1 = "#shadow-root";
String cssSelectorForHost2 = "#inner-shadow-dom";
String cssSelectorForHost3 = "#nested-shadow-dom";
Thread.sleep(1000);
SearchContext shadow0 = driver.findElement(By.cssSelector("#shadow-root")).getShadowRoot();
Thread.sleep(1000);
SearchContext shadow1 = shadow0.findElement(By.cssSelector("#inner-shadow-dom")).getShadowRoot();
Thread.sleep(1000);
SearchContext shadow2 = shadow1.findElement(By.cssSelector("#nested-shadow-dom")).getShadowRoot();
Thread.sleep(1000);
shadow2.findElement(By.cssSelector("#multi-nested-shadow-element"));


