package com.pinyougou.solrutil;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate SolrTemplate;

    public void importItemData() {

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> itemList = itemMapper.selectByExample(example);

        for (TbItem item : itemList) {
            System.out.println(item.getCategory());
            Map map = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(map);
        }
        SolrTemplate.saveBeans(itemList);
        SolrTemplate.commit();
    }

    public static void main(String[] args) {
		/*ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solr = (SolrUtil) context.getBean("solrUtil");
		solr.importItemData();*/
        //Boolean b;
        HashMap map = new HashMap(1);
       /* map.put(1,1 );
        System.out.println(map.size());
        map.put(17,1 );
        System.out.println(map.size());
        map.put(33,1 );
        System.out.println(map.size());
        map.put(49,1 );
        System.out.println(map.size());
        map.put(65,1 );
        map.put(81,1 );
        map.put(97,1 );
        map.put(113,1 );*/
        for (int i = 0; i < 170000; i++) {
            map.put(i, i);
        }
    }

}
