# MyDragView
A DragView similar to the one in Google Map used to display place details.

![](https://raw.githubusercontent.com/PeiyaoChen/MyDragView/master/MyDragViewDemo.gif)

Usage
===============================

1. Add a view(only one) that you want to drag from the bottom in this widget in layout XML:

```java
    <com.example.cpy.draghelpertest.MyDragView
        android:id="@+id/dragview"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:orientation="vertical"
        android:background="#00000000">
        >
        <LinearLayout
            android:id="@+id/view_to_drag"
            android:layout_width="fill_parent"
            android:layout_height="fill_parent"
            android:background="#FFFFFF"
            android:orientation="vertical"
            >
            <View
                android:id="@+id/header"
                android:layout_width="fill_parent"
                android:background="#00FF0000"
                android:layout_height="100dp" />
            <Button
                android:id="@+id/button"
                android:layout_width="50dp"
                android:layout_height="50dp"
                android:clickable="true"
                />
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="HELLO"
                />
        </LinearLayout>
    </com.example.cpy.draghelpertest.MyDragView>
````


2. Add header through {@link #setHeader(int)} or {@link #setHeader(View)} which will be display after calling {@link #show()}:

```java
    dragView.setHeader(R.id.header);
````

3. Set the middle height of the view through {@link #setMiddleDisHeight(int)}:

```java
    final int middleHeight = getResources().getDisplayMetrics().heightPixels / 2;
        dragView.setMiddleDisHeight(middleHeight);
````

4. Call {@link #setOnPositionChangedListener(OnPositionChangedListener)} and {@link #setOnStopLevelChangedListener(OnStopLevelChangedListener)} to customize the action of your view:

```java
    dragView.setOnPositionChangedListener(new MyDragView.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int top) {
                int a = 0;
                if(top < dragView.getHeight() - middleHeight) {
                    a = 255;
                }
                else if(top < dragView.getHeight() - header.getHeight()){
                    a = (int) ((dragView.getHeight() - header.getHeight() - top) / (float)(middleHeight - header.getHeight()) * 255);
                }
                else {
                    a = 0;
                }
                int color = Color.argb(a, 255, 0, 0);
                header.setBackgroundColor(color);
            }
        });
````
