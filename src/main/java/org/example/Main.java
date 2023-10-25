package org.example;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        //part1
        //args
        /*2010-01-01
        1422396,1450759,1449192,1451562*/
        if (args.length == 2) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date;
            try {
                date = formatter.parse(args[0]);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            List<String> listOfId = Arrays.stream(args[1].split(",")).toList();

            part1(formatter, listOfId, date);
        }
        //end of part1

        //part2
        part2();
        //end of part2
    }

    private static void part1(SimpleDateFormat formatter, List<String> listOfId, Date date) {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream("D:\\AS_ADDR_OBJ.XML"));
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("OBJECT")) {
                        String currentId = startElement.getAttributeByName(new QName("OBJECTID")).getValue();
                        try {
                            Date currentStartDate = formatter.parse(startElement.getAttributeByName(new QName("STARTDATE")).getValue());
                            Date currentEndDate = formatter.parse(startElement.getAttributeByName(new QName("ENDDATE")).getValue());
                            if (listOfId.contains(currentId)&&(date.compareTo(currentStartDate)>=0)&& (date.compareTo(currentEndDate)<=0)) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(currentId);
                                stringBuilder.append(": ");
                                stringBuilder.append(startElement.getAttributeByName(new QName("TYPENAME")).getValue());
                                stringBuilder.append(" ");
                                stringBuilder.append(startElement.getAttributeByName(new QName("NAME")).getValue());
                                System.out.println(stringBuilder.toString());
                            }
                        } catch (ParseException e) {
                            //нет ошибок на текщих данных
                            return;
                        }
                    } else if (startElement.getName().getLocalPart().equals("ADDRESSOBJECTS")) {
                        continue;
                    } else {
                        //нет вывода на текщих данных
                        System.out.println(startElement.getName().getLocalPart());
                    }
                }
                if (nextEvent.isEndElement()) {
                    EndElement endElement = nextEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("OBJECT")||endElement.getName().getLocalPart().equals("ADDRESSOBJECTS")) {
                        continue;
                    } else {
                        //нет вывода на текщих данных
                        System.out.println(endElement.getName().getLocalPart());
                    }
                }
            }
            reader.close();
        } catch (XMLStreamException | FileNotFoundException e) {
            //нет ошибок на текщих данных
            throw new RuntimeException(e);
        }
    }

    private static void part2() {

        //список с адресами собирается в ArrayList, в интернете пишут, что сейчас 17 уровней деления адресных объектов.
        //при создании элемента задаю 20 с запасом.
        List<String[]> listOfAnswers = new ArrayList<>();
        //первое число элемент в listOfAnswers второе число позиция конца в элементе в массиве String[]
        Map<Integer, Integer> mapOfAnswers = new HashMap<>();
        //первую строку OBJECTID - ищем, вторая строка заполняем PARENTOBJID найденного
        Map<String, String> mapOfCurrentDataToFind = new HashMap<>();
        //сет со списком текщих PARENTOBJID для реинициализации mapOfCurrentDataToFind
        HashSet<String> setOfNewDataToFind = new HashSet<>();
        //сет со списком всех PARENTOBJID для поиска значений в AS_ADDR_OBJ
        HashSet<String> setOfNewDataToFindInObjects = new HashSet<>();
        //первую строку OBJECTID - ищем, вторая строка заполняем выходные данные из элемента структуры
        Map<String, String> mapOfNewDataToFindInObjects = new HashMap<>();

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream("D:\\AS_ADDR_OBJ.XML"));
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("OBJECT")) {
                        String currentTypeName = startElement.getAttributeByName(new QName("TYPENAME")).getValue();
                        String isActual = startElement.getAttributeByName(new QName("ISACTUAL")).getValue();
                        String isActive = startElement.getAttributeByName(new QName("ISACTIVE")).getValue();

                        //сменил кодировку для того чтобы соответствовало .equals("проезд") вообще "проезд" надо выносить в настройки
                        try {
                            currentTypeName = new String(currentTypeName.getBytes(StandardCharsets.UTF_8), "windows-1251");
                        } catch (UnsupportedEncodingException e) {
                            continue;
                        }

                        if (currentTypeName.equals("проезд")&&isActual.equals("1")&&isActive.equals("1")) {
                            String currentId = startElement.getAttributeByName(new QName("OBJECTID")).getValue();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(startElement.getAttributeByName(new QName("TYPENAME")).getValue());
                            stringBuilder.append(" ");
                            stringBuilder.append(startElement.getAttributeByName(new QName("NAME")).getValue());

                            String addrInit = stringBuilder.toString();
                            String[] row = new String[20];
                            row[0] = addrInit;
                            row[1] = currentId;
                            for (int i = 2; i < 20; i++) {
                                row[i]="";
                            }
                            listOfAnswers.add(row);
                            mapOfAnswers.put(listOfAnswers.size()-1, 2);
                            setOfNewDataToFind.add(currentId);
                        }
                    } else if (startElement.getName().getLocalPart().equals("ADDRESSOBJECTS")) {
                        continue;
                    } else {
                        //нет вывода на текущих даннных
                        System.out.println(startElement.getName().getLocalPart());
                    }
                }
                if (nextEvent.isEndElement()) {
                    EndElement endElement = nextEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("OBJECT")||endElement.getName().getLocalPart().equals("ADDRESSOBJECTS")) {
                        continue;
                    } else {
                        //нет вывода на текущих даннных
                        System.out.println(endElement.getName().getLocalPart());
                    }
                }
            }
            reader.close();
        } catch (XMLStreamException | FileNotFoundException e) {
            //нет ошибок на текущих даннных
            throw new RuntimeException(e);
        }

        try {
            int n=0;
            //всего 17 уровней иерархии в крайнем случае будет 17 проходов
            //на текущих данных проходов 5
            while (!setOfNewDataToFind.isEmpty()) {
                //сброс данных
                if (!mapOfCurrentDataToFind.isEmpty()) {
                    for (int i = 0; i < listOfAnswers.size(); i++) {
                        String[] row = listOfAnswers.get(i);
                        Integer position = mapOfAnswers.get(i);
                        String current = row[position-1];
                        if (mapOfCurrentDataToFind.containsKey(current)) {
                            row[position] = mapOfCurrentDataToFind.get(current);
                            mapOfAnswers.put(i, position+1);
                        }
                    }
                    mapOfCurrentDataToFind.clear();
                }
                for (String str : setOfNewDataToFind) {
                    mapOfCurrentDataToFind.put(str, "");
                }
                setOfNewDataToFind.clear();

                //reader нельзя обнулить позицию, проще создать новый, есть ResettableXmlEventReader, но это нужно почитать
                XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream("D:\\AS_ADM_HIERARCHY.XML"));
                while (reader.hasNext()) {
                    XMLEvent nextEvent = reader.nextEvent();
                    if (nextEvent.isStartElement()) {
                        StartElement startElement = nextEvent.asStartElement();
                        if (startElement.getName().getLocalPart().equals("ITEM")) {
                            String currentId = startElement.getAttributeByName(new QName("OBJECTID")).getValue();
                            String isActive = startElement.getAttributeByName(new QName("ISACTIVE")).getValue();

                            if (mapOfCurrentDataToFind.containsKey(currentId) && isActive.equals("1")) {
                                String parentId = startElement.getAttributeByName(new QName("PARENTOBJID")).getValue();
                                //заполняем иерархию
                                //решил не усложнять, но можно добавлять текущий parentId для поиска
                                //единственно, что отследить, что поиск прошел хотя бы полный раз
                                mapOfCurrentDataToFind.put(currentId, parentId);
                                //заполняем входные данные на следующий проход
                                setOfNewDataToFind.add(parentId);
                                //заполняем входные данные на поиск названий
                                setOfNewDataToFindInObjects.add(parentId);
                            }
                        } else if (startElement.getName().getLocalPart().equals("ITEMS")) {
                            continue;
                        } else {
                            System.out.println(startElement.getName().getLocalPart());
                        }
                    }
                    if (nextEvent.isEndElement()) {
                        EndElement endElement = nextEvent.asEndElement();
                        if (endElement.getName().getLocalPart().equals("ITEMS") || endElement.getName().getLocalPart().equals("ITEM")) {
                            continue;
                        } else {
                            System.out.println(endElement.getName().getLocalPart());
                        }
                    }
                }
                reader.close();
                System.out.println(n);
                if (n==100000) n=0;
                n++;
            }
            //получили массив listOfAnswers с иерархией адреса от конца к началу
            //получили сет setOfNewDataToFindInObjects для получения данных
            for (String str : setOfNewDataToFindInObjects) {
                mapOfNewDataToFindInObjects.put(str, "");
            }
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream("D:\\AS_ADDR_OBJ.XML"));
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("OBJECT")) {
                        String currentId = startElement.getAttributeByName(new QName("OBJECTID")).getValue();
                        String isActual = startElement.getAttributeByName(new QName("ISACTUAL")).getValue();
                        String isActive = startElement.getAttributeByName(new QName("ISACTIVE")).getValue();
                        if (mapOfNewDataToFindInObjects.containsKey(currentId)&&isActual.equals("1")&&isActive.equals("1")) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(startElement.getAttributeByName(new QName("TYPENAME")).getValue());
                            stringBuilder.append(" ");
                            stringBuilder.append(startElement.getAttributeByName(new QName("NAME")).getValue());
                            mapOfNewDataToFindInObjects.put(currentId, stringBuilder.toString());
                        }
                    } else if (startElement.getName().getLocalPart().equals("ADDRESSOBJECTS")) {
                        continue;
                    } else {
                        //нет вывода на текущих даннных
                        System.out.println(startElement.getName().getLocalPart());
                    }
                }
                if (nextEvent.isEndElement()) {
                    EndElement endElement = nextEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("OBJECT")||endElement.getName().getLocalPart().equals("ADDRESSOBJECTS")) {
                        continue;
                    } else {
                        //нет вывода на текущих даннных
                        System.out.println(endElement.getName().getLocalPart());
                    }
                }
            }
            reader.close();

            for (String[] sArr:
                    listOfAnswers) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < sArr.length-2; i++) {
                    String value = sArr[sArr.length -i-1];
                    if (!value.isEmpty()&&!value.equals("0")) {
                        stringBuilder.append(mapOfNewDataToFindInObjects.get(value));
                        stringBuilder.append(", ");
                    }
                }
                stringBuilder.append(sArr[0]);
                System.out.println(stringBuilder.toString().trim());
            }
        } catch (XMLStreamException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}