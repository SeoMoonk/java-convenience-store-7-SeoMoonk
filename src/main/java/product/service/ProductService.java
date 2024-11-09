package product.service;

import static global.utils.StringParser.parseInt;

import global.constants.FileType;
import global.utils.FileParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import product.constants.ProductPresetKeys;
import product.dto.response.ProductInfo;
import product.entity.Product;
import product.repository.ProductRepository;
import promotion.entity.Promotion;
import promotion.service.PromotionService;

public class ProductService {

    private final PromotionService promotionService;
    private final ProductRepository productRepository;

    public ProductService(PromotionService promotionService, ProductRepository productRepository) {
        this.promotionService = promotionService;
        this.productRepository = productRepository;
    }

    public void loadFromFilePath(String filePath) {
        List<Map<String, String>> productFileDataSets = FileParser.parsingByFilePath(filePath, FileType.PRODUCT);
        for (Map<String, String> dataSet : productFileDataSets) {
            createByDataSet(dataSet);
        }
    }

    private void createByDataSet(Map<String, String> dataSet) {
        String name = dataSet.get(ProductPresetKeys.PRODUCT_NAME_PRESET_KEY.getKey());
        int price = parseInt(dataSet.get(ProductPresetKeys.PRODUCT_PRICE_PRESET_KEY.getKey()));
        int quantity = parseInt(dataSet.get(ProductPresetKeys.PRODUCT_QUANTITY_PRESET_KEY.getKey()));
        String promotionName = dataSet.get(ProductPresetKeys.PRODUCT_PROMOTION_NAME_PRESET_KEY.getKey());
        Promotion promotionByName = getPromotionByName(promotionName);

        Product product = new Product(name, price, quantity, promotionByName);
        productRepository.save(product);
    }

    public Product getByName(String name) {
        Optional<Product> maybeProduct = productRepository.findByName(name);
        if (maybeProduct.isEmpty()) {
            //FIXME: 예외 유형 확인
            throw new IllegalArgumentException("해당 이름을 가진 제품을 찾을 수 없습니다");
        }
        return maybeProduct.get();
    }

    private Promotion getPromotionByName(String promotionName) {
        Promotion promotion;
        try {
            promotion = promotionService.getByName(promotionName);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return promotion;
    }

    public List<ProductInfo> getProductInfos() {
        List<Product> products = getAll();
        List<ProductInfo> productInfos= new ArrayList<>();
        for(Product product : products) {
            productInfos.add(ProductInfo.fromProduct(product));
        }

        return productInfos;
    }

    private List<Product> getAll() {
        return productRepository.findAll();
    }
}
