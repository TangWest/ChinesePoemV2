# 《阅诗》
## 一款供用户在线浏览古诗词的app

一、	作品描述
1.	作品介绍</br>
阅诗是一款供用户在线浏览古诗词的app。古诗词是中华文化的瑰宝，阅读古诗词可以陶冶情操，提高文学素养。这款app首先面向青少年中小学生，能够帮助他们提高写作水平，提高语文成绩。第二目标用户是中国文化的爱好者，他们可以利用零碎时间感受诗词的魅力。</br>
2.	功能</br>
（1）首页：展示古诗词列表，古诗词会随着数据来源网站更新而更新，保证有新的内容，如重阳节等节日会推送相关主题诗词。</br>
（2）搜索：搜索古诗词题目中的关键词将展示结果列表。</br>
（3）诗词详情：点击古诗词列表中的项目，将跳转到诗词详情页，这里有古诗词内容及鉴赏，还可以记录笔记。</br>
（4）笔记：阅读每一首诗都有心得，每读一遍都有不同的感受，可以记录笔记。按照时间由新到旧显示笔记列表，长按项目可删除。</br>
（5）收藏：在详情页收藏住当下心动，以防古诗词列表更新后与喜欢的诗词遗憾错过。收藏后的古诗词将显示在收藏列表。</br>
</br>
二、	技术要点</br>
1.	使用技术</br>
（1）	爬取网站内容使用Jsoup解析，app的数据来源是https://www.shicimingju.com/ 的首页及搜索页的内容，包括诗词的题目、作者、内容以及鉴赏。</br>
（2）	SQLite数据库。创建了两张表，分别记录收藏的诗，和某首诗对应的笔记。</br>
（3）	主页面ui采用了ViewPager+fragment，完成滑动效果。</br>
2.	完成难点</br>
1.	将用户需求落地：在做完第一版app后，我觉得在随时更新的古诗词中，需要添加收藏功能，收藏后也从某个方面限制用户将笔记都记在已收藏的诗上，不会记笔记后找不到那首诗了。考虑到显示的收藏列表和首页列表结构类似，于是我打算用tab的UI将这两个模块结合起来，所以又重新写了一遍UI。</br>
2.	编程的工作方法：不是如何写代码，而是理清思路，先实现什么后实现什么。理清楚就比较简单了。</br>
3.	学习小结</br>
面对新知识不要有畏惧心理，要提高自己的学习能力。在大一大二我接触过andriod编程，当时感觉一头雾水，但是当时的试水让我在终于来临的正式课程中有所准备。就在课程中和项目中，要多用google已经写好的功能，比如toolbar能让页面上方功能一目了然。</br>
</br>
三、截图</br>
![image](https://github.com/TangWest/ChinesePoemV2/blob/master/1.gif)
