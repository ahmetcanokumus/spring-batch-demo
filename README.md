# Spring Batch Demo

Bu proje, PostgreSQL'deki urun verisini Spring Batch ile okuyup is kurallarindan gecirerek Elasticsearch index'ine aktaran bir demo uygulamasidir.

## Kullanilan Teknolojiler

- Java 21
- Spring Boot 3.4.4
- Spring Batch
- Spring Data JPA (Hibernate)
- Spring Data Elasticsearch
- PostgreSQL
- Maven
- Lombok

## Proje Akisi

Uygulama tek adimli (`chunk`) bir batch job calistirir:

1. `BatchController` icindeki `POST /api/batch/sync-products` endpoint'i job'i tetikler.
2. `productReader` PostgreSQL'den su kriterlerle urunleri ceker:
   - `variantType = COLOR`
   - `approvalStatus = APPROVED`
   - Stok (`stockLevel.available`) 0'dan buyuk
3. `ProductItemProcessor` her urunu `ProductDocument` formatina donusturur:
   - Varyantlardan sadece stokta olanlar secilir
   - Gecerli tarih araligindaki aktif fiyat bulunur
   - Hic uygun varyant yoksa kayit atlanir (`null` doner)
4. `elasticsearchWriter` donusen dokumanlari `products` index'ine yazar.
5. `ProductSkipListener` okuma/isleme/yazma asamalarindaki skip durumlarini loglar.

## Veri Modeli (Ozet)

- `Product`: Ana urun ve varyant yapisi (parent-child iliskisi)
- `StockLevel`: Urun stok bilgisi
- `PriceRow`: Tarih aralikli fiyat bilgisi
- `ProductDocument`: Elasticsearch'e yazilan dokuman
- `ProductSizeVariant`: Dokuman icindeki boyut/fiyat/stok alt modeli

## Calistirma On Kosullari

Asagidaki servislerin lokal ortamda calisiyor olmasi gerekir:

- PostgreSQL (`jdbc:postgresql://localhost:5432/batchdb`)
- Elasticsearch (`http://localhost:9200`)

Varsayilan ayarlar `src/main/resources/application.properties` dosyasindadir:

- Veritabani kullanicisi/sifresi: `postgres/postgres`
- Uygulama portu: `8080`
- Batch schema initialization: `always`
- Otomatik job calistirma: `false` (job endpoint ile manuel tetiklenir)

## Uygulamayi Calistirma

```bash
mvn spring-boot:run
```

## Batch Job Tetikleme

Uygulama ayakta iken asagidaki endpoint cagrilabilir:

```bash
curl -X POST http://localhost:8080/api/batch/sync-products
```

Basarili durumda `jobName`, `executionId`, `status` ve `startTime` bilgileri doner.

## Notlar

- Job adi: `productToElasticJob`
- Step adi: `productToElasticStep`
- Step, fault-tolerant calisir; Elasticsearch hatalarinda skip/retry mekanizmasi vardir.
