# π νλ‘μ νΈ μκ°
>λΌμ΄μ§ μΊ νκΈ°κ°(μ½ 2μ£Ό) λμ νΌμμ μ§νν [μ νλΈ](https://www.youtube.com/) ν΄λ‘  νλ‘μ νΈμλλ€.  
- μ μ κΈ°κ° : 2022λ 12μ 11μΌ ~ 12μ 25μΌ  
- μλ² κ°λ°μ : λμ€(λ³ΈμΈ)

</br>

## πββοΈ Wiki
- π° [API λͺμΈμ](https://docs.google.com/spreadsheets/d/1JuW5yt8tvZ3sx_hiWTesqtDn_ihmU_4J/edit#gid=514363059 )
- π¦ [ERD μ€κ³λ](https://aquerytool.com/aquerymain/index/?rurl=f9df6444-acbe-4991-a7d8-c5f6fd088abd)    
    - λΉλ°λ²νΈ : 738qku    
- π [λλ ν λ¦¬ κ΅¬μ‘°]: μΆκ°μμ 
- π½ μμ° μμ : μΆκ°μμ 


</br>

## π  μ¬μ© κΈ°μ 
#### `Back-end`
  - Java 15
  - Spring Boot 2.4.2 (μννΈμ€νμ΄λ Template μ¬μ©)
  - Gradle 6.7.1
  - Spring JDBC 
  - 
#### `DevOps`  
  - AWS EC2 (Ubuntu 20.04)  
  - AWS RDS (Mysql 8.0)
  - Nginx
  - GitHub
#### `Etc`  
  - JWT

</br>

## π¦ ERD μ€κ³λ
![Youtube_modeling(Final)](https://user-images.githubusercontent.com/62496215/157594667-bdfef997-3913-4eb5-bda8-f696f0c790a7.png)
</br>

</br>


## π ν΅μ¬ κΈ°λ₯ λ° λ΄λΉ κΈ°λ₯
>λΉκ·Όλ§μΌ μλΉμ€μ ν΅μ¬κΈ°λ₯μ μ±λ κ΅¬λκ³Ό λμμ μλ‘λ λ° μ‘°νμλλ€.  
>μλΉμ€μ μΈλΆμ μΈ κΈ°λ₯μ [API λͺμΈμ](https://docs.google.com/spreadsheets/d/1JuW5yt8tvZ3sx_hiWTesqtDn_ihmU_4J/edit#gid=514363059)λ₯Ό μ°Έκ³ ν΄ μ£Όμλ©΄ κ°μ¬ν©λλ€.   
- κ΅¬νν κΈ°λ₯  
    - μ¬μ©μ : νμκ°μ API, λ‘κ·ΈμΈ/λ‘κ·Έμμ API, νλ‘ν μ‘°νβμμ  API
    - μ±λ : μ±λ κ°μβκ΅¬λβμ‘°νβλ³κ²½βμ­μ  API
    - νλ¦¬λ―Έμ : νλ¦¬λ―Έμ κ°μβλ³κ²½βμ·¨μ API
    - λμμ : λμμ μμ±βμ‘°νβλ³κ²½βμ­μ  API   

</br>


## π νΈλ¬λΈ μν
- DB μ°κ²° μ λ³΄, JWTμ PASSWORD ν€ κ°μ΄ λΈμΆλμ§ μλλ‘ .gitignore νμΌμ Secret.java, application.xml μΆκ°
- νμκ°μ μ΄μΈμ ν¨μ€μλ λ³κ²½μμλ μνΈννμ¬ DBμ μ μ₯νλλ‘ μ½λ κ΅¬ν

</br>


## β νκ³  / λλμ 
>νλ‘μ νΈ κ°λ° νκ³  κΈ   
- Spring bootλ₯Ό μ²μ μ νκ³  2μ£Ό μμ μ νλΈ API μλ²λ₯Ό κ°λ°ν΄μΌ νλ μν©μμ Spring bootμ λμκ³Όμ  (Controller -> Service/Provider -> Dao)μμμ κΈ°λ³Έ μ½λλ₯Ό μ΄ν΄νλλ° 3μΌ μ λμ μκ°μ΄ μμλμμ΅λλ€.
- κ·Έλ¦¬κ³  localμ΄ μλ EC2 νκ²½μμ remote λͺ¨λλ‘ Intellijλ₯Ό ν΅ν΄ κ°λ°μ μ§ννμλλ°, Intellijμμ remote λͺ¨λλ‘ κ°λ°μ μ§νμ κΈ°λ³Έμ μΈ μ½λ λ¬Έλ² μ€λ₯λ₯Ό μ²΄ν¬ν΄ μ£Όμ§ μμ λΉλ κ³Όμ μμμ λ§μ μ€λ₯κ° λ°μνμ¬ μ€λ₯λ₯Ό μ°Ύμ ν΄κ²°νλλ°μλ λ§μ μκ°μ΄ μμλμμ΅λλ€.
- μ²μμ Spring bootλ₯Ό μ νμλ, 20κ° κ°λμ APIλ₯Ό 2μ£ΌμΌ μμ κ°λ°ν΄μΌ νλ κ³Όμ μ λν λΆλ΄κ°κ³Ό μλ°κ°μ΄ ν¬κ² λκ»΄μ‘μΌλ, μ λ ν¬κΈ°νμ§ μλλ€λ λ§μΈλ νλλ§μΌλ‘ νλ£¨ 14μκ° μ λλ₯Ό ν¬μνμ¬ μ νλΈμ κΈ°λ³Έμ μΈ API λ€μ κ΅¬νν  μ μμμ΅λλ€.




 
