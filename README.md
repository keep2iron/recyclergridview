

# RecyclerGridView

![](https://api.bintray.com/packages/keep2iron/maven/recyclergridview/images/download.svg) ![BuildStatus](https://travis-ci.org/keep2iron/recyclergridview.svg?branch=master)

![](images/preview.png)

RecyclerGridView is an nine gridlayout.

* support in RecyclerView with RecyclerPool
* good performance(with RecyclerView pool)
* support multiple type
* flexable

### gradle

````groovy
implementation 'io.github.keep2iron:recyclergridview:$last_version'
````

### preview

sample code in **app** project.

![](images/demo.gif)

### core class

- **Condition.kt**

  it can let RecyclerGridView know how to layout.overload some method you can change the show style.

  |     Method/Property      |                            Effect                            |
  | :----------------------: | :----------------------------------------------------------: |
  |        maxColumn         |             column count ,**default value is 9**             |
  |       aspectRatio        |       single item aspect ratio,**default value is 1f**       |
  | maxPercentLayoutInParent | controll the total width proportion in layout,**default value is 9** |
  |       maxShowCount       |         max count,**more than it does not display**          |
  |  weatherConditionApply   |             return true above of al will effect              |

  Default implementation

  **SingleCondition**,it will work in a single image.

  **FourXFourCondition**,it will work in **four count image**.

  

### usage

1.init RecyclerView with ViewPool

![](images/code_exp1.png)

2.Create your RecyclerView Adapter and bind viewPool

![](images/code_exp3.png)

3.create RecyclerGridAdapter,you can set multiple type view,if you want to add multiple you can follow this way.

**getItemViewType() return defualt type must call super.getItemViewType()**

![](images/code_exp4.png)