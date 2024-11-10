package product.service;

import static global.constants.GlobalStatic.ERROR_MSG_PREFIX;
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
import store.dto.request.PurchaseRequest;

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
        Promotion promotionByName = getPromotionByNameForProduct(promotionName);

        Product product = new Product(name, price, quantity, promotionByName);
        productRepository.save(product);
    }

    public Product getByName(String name) {
        Optional<Product> maybeProduct = productRepository.findByName(name);
        if (maybeProduct.isEmpty()) {
            throw new IllegalArgumentException(ERROR_MSG_PREFIX + "해당 이름을 가진 제품을 찾을 수 없습니다 : " + name);
        }
        return maybeProduct.get();
    }

    private Promotion getPromotionByNameForProduct(String promotionName) {
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
        List<ProductInfo> productInfos = new ArrayList<>();
        for (Product product : products) {
            productInfos.add(ProductInfo.fromProduct(product));
        }
        return productInfos;
    }

    private List<Product> getAll() {
        return productRepository.findAll();
    }

    public List<Product> getAllByName(String name) {
        List<Product> products = productRepository.findAllByName(name);
        if (products.isEmpty()) {
            throw new IllegalArgumentException(ERROR_MSG_PREFIX + "해당 이름을 가진 제품을 찾을 수 없습니다" + name);
        }
        return new ArrayList<>(products);
    }

    public int getAllQuantityByName(String name) {
        return productRepository.countAllByName(name);
    }

    public boolean isPromotionTargetRequest(List<Promotion> promotions, PurchaseRequest request) {
        for(Promotion promotion : promotions) {
            Optional<Product> maybeProduct = getByNameAndPromotion(request.productName(), promotion);

            if(maybeProduct.isPresent()) {
                int minQuantity = promotion.getBonusQuantity() + promotion.getConditionQuantity();
                return maybeProduct.get().getQuantity() >= minQuantity; //최소 한번 프로모션 적용이 되면 대상이 됨
            }
        }
        return false;
    }

    public Optional<Product> getByNameAndPromotion(String name, Promotion promotion) {
        return productRepository.findByNameAndPromotion(name,  promotion);
    }

    public Product getByNameAndHasPromotion(String name) {
        Optional<Product> maybeProduct = productRepository.findByNameAndHasPromotion(name);

        if(maybeProduct.isEmpty()) {
            throw new IllegalArgumentException("상품 정보를 조회할 수 없습니다 : " + name);
        }

        return maybeProduct.get();
    }
}
