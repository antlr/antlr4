# ANTLR 4 Dökümantasyonu

Lütfen Stackoverflow'da veya antlr tartışma listesinde soru sormadan önce [Sık sorulan sorular (SSS)](faq/index.md) 'ı inceleyin.

Notlar:
<ul> 
<li>Bu dökümantasyona katkıda bulunmak istiyorsanız, <a href=https://github.com/antlr/antlr4>antlr/antlr4 repo</a>sunu <a href=https://help.github.com/articles/fork-a-repo>fork</a>layın. Ardından `doc/index.md` veya bu klasördeki dosyalara katkıda bulunun. Ana repo için bir <a href=https://help.github.com/articles/creating-a-pull-request>pull request</a> oluşturun. Kod ve dökümantasyon için güncellediğiniz dosyaları tek bir <a href=https://help.github.com/articles/creating-a-pull-request>pull request</a> ile bildirmeyin. <b>Eğer daha önce pull request bildirmediyseniz kendinizi [contributors.txt](https://github.com/antlr/antlr4/blob/master/contributors.txt) dosyasına kayıt etmeniz gerekir.</b></li>

<li>Copyright © 2012, The Pragmatic Bookshelf.  Pragmatic Bookshelf grants a nonexclusive, irrevocable, royalty-free, worldwide license to reproduce, distribute, prepare derivative works, and otherwise use this contribution as part of the ANTLR project and associated documentation.</li>

<li>Bu metnin çoğu parçası  izinleri dahilinde <a href=http://pragprog.com/book/tpantlr2/the-definitive-antlr-4-reference>The Definitive ANTLR 4 Reference</a> kitabından kopyalanmıştır. Aradan geçen zaman içinde değişen araçlara göre biçimlendirilmiştir.</li>
</ul>

Dökümantasyonda ki linkler kitabın çeşitli bölümlerine atıfta bulunur ve kitabın yayıncı sitesindeki sayfasına yönlendirir. Yayıncı websitesinde kitabı almadan okuyabileceğiniz iki alıntı mevcut: [Dinleyici ile bir Çevirici oluşturmak (İngilizce)](http://media.pragprog.com/titles/tpantlr2/listener.pdf), [Hadi Meta Verisini Alalım (İngilizce)](http://media.pragprog.com/titles/tpantlr2/picture.pdf). Ayrıca aşağıda gösterilen kitapları okumayıda düşünebilirsiniz (video referans kitabının incelemesini içerir):
<a href=""><img src=images/tpantlr2.png width=120></a>
<a href=""><img src=images/tpdsl.png width=120></a>
<a href="https://www.youtube.com/watch?v=OAoA3E-cyug"><img src=images/teronbook.png width=250></a>

Bu dökümantasyon ANTLR gramerinin anahtar kısımlarına ve gramer sözdizimine bir referans oluşturur ve özet geçer.
Kitapta ki tüm örnek kaynak kodlar, sadece bu bölüm için değil, yayıncı websitesinde ücretsiz erişilebilir durumdadır. Aşağıda ki video genel olarak ANTLR4'ü anlatır ve parse tree listeners kullanarak java dosyalarının nasıl kolayca işleneceğine dair bir açıklama içerir.

<a href="https://vimeo.com/59285751"><img src=images/tertalk.png width=200></a>

Bunları Java ile kullanmak için Andreas Stefik'in [Intellij için ANTLR ayarlama notları](https://docs.google.com/document/d/1gQ2lsidvN2cDUUsHEkT05L-wGbX5mROB7d70Aaj3R64/edit#heading=h.xr0jj8vcdsgc)'nı okuyabilirsiniz.

## Başlıklar

* [ANTLR v4'e Başlarken](getting-started.md)

* [Gramer Sözlüğü](lexicon.md)

* [Gramer Yapısı](grammars.md)

* [Parser Kuralları](parser-rules.md)

* [Sol-recursive Kuralları](left-recursion.md)

* [Aksiyon ve Nitelikler](actions.md)

* [Lexer Kuralları](lexer-rules.md)

* [Joker Operatörü ve Beklentisiz Alt kuralları](wildcard.md)

* [Parse Tree Listeners](listeners.md)

* [Parse Tree Eşleşmesi ve XPath](tree-matching.md)

* [Semantik Yüklemler](predicates.md)

* [Ayarlar](options.md)

* [ANTLR Aracı Komut Satırı Ayarları](tool-options.md)

* [Çalışma Zamanı Kütüphaneleri ve Kod Oluşturma Hedefleri](targets.md)

* [Unicode U+FFFF, U+10FFFF karakter akışları](unicode.md)

* [İkili Akışları Parse Etmek](parsing-binary-files.md)

* [Harf Büyük-Küçük Fark Etmeksizin Lexer İşlemi](case-insensitive-lexing.md)

* [Parser ve lexer Yorumlayıcıları](interpreters.md)

* [Kaynaklar](resources.md)

# ANTLR inşa etmek 

* [ANTLR'ı Kendi Başına İnşa Etmek](building-antlr.md)

* [ANTLR'a Katkıda Bulunmak](/CONTRIBUTING.md)

* [ANTLR Release Sürümünü Değiştirmek](releasing-antlr.md)

* [ANTLR Projesi Unit testleri](antlr-project-testing.md)

* [ANTLR İçin Hedef Dil Oluşturmak](creating-a-language-target.md)
