package product.service;

import static global.utils.StringParser.parseInt;

import global.dto.response.FileParsedResponse;
import global.utils.FileParser;
import java.util.List;
import java.util.Optional;
import product.entity.Product;
import product.repository.ProductRepository;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public void setUp(String filePath) {
        FileParsedResponse fileParsedResponse = FileParser.parsingByFilePath(filePath);

        List<List<String>> values = fileParsedResponse.values();

        for(List<String> value: values) {
            create(value);
        }
    }

    private void create(List<String> value) {
        Product product = new Product(value.get(0), parseInt(value.get(1)), parseInt(value.get(2)), value.get(3));
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
}
