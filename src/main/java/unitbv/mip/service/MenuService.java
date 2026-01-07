package unitbv.mip.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import unitbv.mip.model.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MenuService {
    private final ObjectMapper mapper;

    public MenuService() {
        this.mapper = new ObjectMapper();
    }

    public void exportMenuToJson(List<Product> products, File file) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, products);
    }

    public List<Product> importMenuFromJson(File file) throws IOException {
        Product[] products = mapper.readValue(file, Product[].class);
        return Arrays.asList(products);
    }
}