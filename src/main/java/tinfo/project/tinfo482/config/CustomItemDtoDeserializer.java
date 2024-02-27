package tinfo.project.tinfo482.config;

import com.nimbusds.jose.shaded.gson.JsonDeserializationContext;
import com.nimbusds.jose.shaded.gson.JsonDeserializer;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonParseException;
import tinfo.project.tinfo482.dto.inventory.AccDto;
import tinfo.project.tinfo482.dto.inventory.FlowerDto;
import tinfo.project.tinfo482.dto.inventory.ItemDto;
import tinfo.project.tinfo482.entity.inventory.Acc;
import tinfo.project.tinfo482.entity.inventory.Flower;

import java.lang.reflect.Type;

public class CustomItemDtoDeserializer implements JsonDeserializer<Object> {
    @Override
    public Object deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Long id = json.getAsJsonObject().get("id").isJsonNull() ? null : json.getAsJsonObject().get("id").getAsLong();
        String category = json.getAsJsonObject().get("category").isJsonNull() ? null :json.getAsJsonObject().get("category").getAsString() ;
        String content = json.getAsJsonObject().get("content").isJsonNull() ? null : json.getAsJsonObject().get("content").getAsString();
        Long price = json.getAsJsonObject().get("price").isJsonNull()? null : json.getAsJsonObject().get("price").getAsLong();
        String name = json.getAsJsonObject().get("name").isJsonNull() ? null :  json.getAsJsonObject().get("name").getAsString();
        int stock = json.getAsJsonObject().get("stock").isJsonNull()? null : json.getAsJsonObject().get("stock").getAsInt();
        String img_url = json.getAsJsonObject().get("img_url").isJsonNull() ? null : json.getAsJsonObject().get("stock").getAsString();
        boolean delivery = json.getAsJsonObject().get("delivery").isJsonNull() ? null : json.getAsJsonObject().get("stock").getAsBoolean();

        //if category exists, it's flower
       if(category !=null){
           return FlowerDto.builder()
                   .id(id)
                   .category(category)
                   .delivery(delivery)
                   .content(content)
                   .name(name)
                   .price(price)
                   .stock(stock)
                   .img_url(img_url)
                   .build();
       }else{

           return AccDto.builder()
                   .id(id)
                   .content(content)
                   .name(name)
                   .price(price)
                   .stock(stock)
                   .img_url(img_url)
                   .build();
       }


    }
}
