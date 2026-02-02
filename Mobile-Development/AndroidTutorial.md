<div align="center">
  <h1>Android Tutorial</h1>
  <small>
    <strong>Author:</strong> Nguyen Tan Phat
  </small> <br />
  <sub>January 29, 2026</sub>
  <div>
    Follow the full video tutorial on
    <a href="https://www.youtube.com/watch?v=_bv36SQJuu0" target="_blank"><b>YouTube</b></a>
  </div>
</div>

## Table of Contents

1. [Project Structure in Android Studio](#1-project-structure-in-android-studio)
2. [AndroidManifest Overview](#2-androidmanifest-overview)
3. [UI Components](#3-ui-components)
4. [Layouts](#4-layouts)
5. [Activity](#5-activity)
6. [Fragment](#6-fragment)

## 1. Project Structure in Android Studio

```text
app/
├── manifests/
│   └── AndroidManifest.xml
├── java/
│   ├── com.example.myapp/
│   │   └── MainActivity.java
│   ├── com.example.myapp/ (androidTest)
│   └── com.example.myapp/ (test)
├── res/
│   ├── drawable/
│   ├── layout/
│   ├── mipmap/
│   └── values/
│       ├── colors.xml
│       ├── strings.xml
│       └── themes.xml
└── Gradle Scripts/
```

- `AndroidManifest.xml`:
  - The essential configuration file for the app.
  - Defines the app's package name, permissions (Internet, Camera), and registers application components (Activities, Services).
- `java/`:
  - Contains your Java source code files (`.java`), separated by package names.
  - `com.example.myapp`: Your main source code (Activities, Fragments, Models, Utils).
  - `com.example.myapp` (androidTest): Instrumented tests (run on a device).
  - `com.example.myapp` (test): Unit tests (run on local JVM).
- `res/`:
  - This folder contains non-code resources like UI layouts, images, and text strings.
  - `drawable/`: Images (PNG, JPG) and XML vector icons.
  - `layout/`: XML files that define the UI structure (e.g. `activity_main.xml`).
  - `mipmap/`: Launch icons for different screen densities.
  - `values/`: Simple values to allow for easy localization and theming.
    - `colors.xml`: Define color hex codes.
    - `strings.xml`: Define text strings (crucial for multi-language - support).
    - `themes.xml`: Define the look and feel of the app.
- `Gradle Scripts/`:
  - `build.gradle`: Defines dependencies (libraries), SDK versions (minSdk, targetSdk), and build configuration.

## 2. AndroidManifest Overview

### What is `AndroidManifest.xml`?

`AndroidManifest.xml` is the **central configuration file** of an Android app.

It tells Android OS:

- What the app is
- What components it has
- What permissions it needs
- Which screen starts first
- What hardware/features it uses

Without this file, the app cannot run.

### Structure and Hierarchy

Every Manifest file follows a strict hierarchical structure, nesting specific configurations inside the root element.

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.example.myapp">

    <uses-permission android:name="..." />

    <uses-feature android:name="..." />

    <application
        android:icon="..."
        android:label="..."
        android:theme="...">

        <activity android:name="..."></activity>
        <service android:name="..."></service>
        <receiver android:name="..."></receiver>
        <provider android:name="..."></provider>

    </application>

</manifest>
```

**1. The Root `<manifest>` Tag**

This tag wraps the entire content of the file.

- `xmlns:android`: Defines the standard Android namespace. This is mandatory.
- `xmlns:tools`: Defines the Android Tools namespace, which is used only for design-time (preview) purposes in Android Studio.
- `package`: The unique ID of the application (e.g., `com.example.myapp`). This ID identifies your app on the Google Play Store.

**2. `<uses-permission>` (Requesting Permissions)**

Declares sensitive permissions the app needs to function. If these are not declared, the app will crash when attempting to access the corresponding feature.

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
```

|       Permission        |    Purpose     |
| :---------------------: | :------------: |
|       `INTERNET`        | Network access |
|        `CAMERA`         |  Camera usage  |
| `READ_EXTERNAL_STORAGE` |   Read files   |
| `ACCESS_FINE_LOCATION`  |      GPS       |

**Note:** For "Dangerous Permissions" (Camera, Location, Contacts) on Android 6.0+, you must also implement **Runtime Permissions** in your Java code.

**3. `<application>` (Global Configuration)**

Contains settings that apply to the entire application.

```xml
<application
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:label="@string/app_name"
    android:theme="@style/AppTheme"
    android:allowBackup="true"
    android:supportsRtl="true"
    android:fullBackupContent="@xml/backup_rules"
    android:dataExtractionRules="@xml/data_extraction_rules">
```

|       Attribute       |                                        Meaning                                         |
| :-------------------: | :------------------------------------------------------------------------------------: |
|    `android:name`     |                                Custom Application class                                |
|    `android:icon`     | The app icon displayed on the home screen (points to `mipmap` or `drawable` resources) |
|    `android:label`    |           The display name of the app (usually points to `@string/app_name`)           |
|    `android:theme`    |                 The global color style/theme (points to `themes.xml`)                  |
| `android:allowBackup` |   Allows Google to automatically backup app data to the cloud (recommended: `true`)    |
| `android:supportsRtl` |               Enables support for Right-to-Left languages (like Arabic)                |

**4. `<activity>` (Screens)**

Every screen (Activity) in your app **must** be declared here. If you create a Java Activity file but forget to declare it in the Manifest, the app will crash with an `ActivityNotFoundException`.

```xml
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:screenOrientation="portrait">
</activity>
```

|           Attribute           |                                                                                  Description                                                                                  |
| :---------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|        `android:name`         |                                    The name of the Java class (e.g., `.MainActivity` refers to `MainActivity.java` in the current package)                                    |
|      `android:exported`       | If true, it allows other apps or the OS to launch this activity (Mandatory for the Launcher/Main activity). Otherwise, the activity can only be opened internally by your app |
|        `android:theme`        |                                                                            Activity-specific theme                                                                            |
|  `android:screenOrientation`  |                                            Locks the screen rotation. `portrait`: Always vertical. `landscape`: Always horizontal.                                            |
|     `android:launchMode`      |                                                                                 Task behavior                                                                                 |
| `android:windowSoftInputMode` |                                   Configures the virtual keyboard behavior (e.g., `adjustResize` pushes the UI up when the keyboard opens)                                    |

**5. `<intent-filter>` (Defining Capabilities)**

Nested inside an `<activity>`. It defines what the activity can do. The most common use is defining the **Entry Point** of the app.

```xml
<activity android:name=".MainActivity"
    android:exported="true">

    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>

</activity>
```

- `MAIN` - Entry point
- `LAUNCHER` - App icon launches this activity

**Note:** Only **ONE** activity should have this filter.

Otherwise, it is used to:

- Open activities via intents
- Handle deep links
- Share content

**Example:** Open via browser (Deep Link)

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="https" android:host="example.com" />
</intent-filter>
```

**6. `<uses-feature>` (Feature Declaration)**

```xml
<uses-feature
    android:name="android.hardware.camera"
    android:required="false" />
```

Defines required hardware:

- Camera
- Bluetooth
- NFC

**7. `<service>` (Background Tasks)**

```xml
<service
    android:name=".MyService"
    android:exported="false" />
```

Used for:

- Background music
- Upload/download
- Sync tasks

**8. `<receiver>` (BroadcastReceiver)**

```xml
<receiver android:name=".BootReceiver">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```

Used for:

- System events
- Battery changes
- Network changes

**9. `<provider>` (ContentProvider)**

```xml
<provider
    android:name=".MyProvider"
    android:authorities="com.example.provider"
    android:exported="false" />
```

Used for:

- Sharing data between apps
- Database access

## 3. UI Components

In Android:

- A **View** is the basic UI building block
- Everything you see on screen is a **View** (or **ViewGroup**)

### Common View Attributes

Before diving into specific components, these are the attributes you will use on almost every single UI element.

**1. Layout & Size Properties**

|        Attribute         |                          Description                           |                                      Common Values                                      |
| :----------------------: | :------------------------------------------------------------: | :-------------------------------------------------------------------------------------: |
|       `android:id`       | Unique identifier for the view, used to access it in Java code |                                    `@+id/myViewName`                                    |
|  `android:layout_width`  |                       Width of the view                        | `wrap_content` (fit content), `match_parent` (fill container), `0dp` (constraint match) |
| `android:layout_height`  |                       Height of the view                       |                          `wrap_content`, `match_parent`, `0dp`                          |
| `android:layout_margin`  |        Space outside the view (distance from neighbors)        |                    `8dp`, `16dp`, `marginTop`, `marginBottom`, etc.)                    |
|    `android:padding`     |  Space inside the view (distance between border and content)   |                   `8dp`, `16dp`, `paddingStart`, `paddingTop`, etc.)                    |
| `android:layout_gravity` |                     Position inside parent                     |                 `center`, `center_horizontal`, `center_vertical`, etc.                  |

**2. Visibility & State**

|      Attribute       |     Description     |                                         Common Values                                         |
| :------------------: | :-----------------: | :-------------------------------------------------------------------------------------------: |
| `android:visibility` |  Show or hide view  | `visible` (default), `invisible` (hidden but takes space), `gone` (hidden and takes no space) |
|  `android:enabled`   | Can interact or not |                                       `true` / `false`                                        |
| `android:clickable`  |  Can receive click  |                                       `true` / `false`                                        |
| `android:focusable`  |   Can gain focus    |                                       `true` / `false`                                        |

**3. Background & Appearance**

|              Attribute              |              Description              |                  Common Values                   |
| :---------------------------------: | :-----------------------------------: | :----------------------------------------------: |
|        `android:background`         |   The background color or drawable    | `#FFFFFF`, `@color/blue`, `@drawable/bg_rounded` |
|           `android:alpha`           |        Opacity or Transparency        |    `0.0` (invisible) to `1.0` (fully visible)    |
|         `android:elevation`         | Z-axis shadow depth (Material Design) |                   `4dp`, `8dp`                   |
|         `android:rotation`          |              Rotate view              |             `90`, `-90`, `180`, etc.             |
| `android:scaleX` / `android:scaleY` |              Scale view               |        `0.5`, `1.5`, `-1.0` (flip), etc.         |

### TextView

Displays **static text** on the screen.

|      Attribute      |                           Description                           |
| :-----------------: | :-------------------------------------------------------------: |
|   `android:text`    |       The string to display, use `@string/name` resource        |
| `android:textColor` |                   Font color (e.g., #000000)                    |
| `android:textSize`  |            Font size, always use `sp` (e.g., `16sp`)            |
| `android:textStyle` |                  Font style: `bold` / `italic`                  |
|  `android:gravity`  |                         Text alignment                          |
| `android:maxLines`  |                       Max number of lines                       |
| `android:ellipsize` | How to handle text overflow (e.g., `end` puts `...` at the end) |

### EditText

Allows **user input**.

|        Attribute        |                                    Description                                    |
| :---------------------: | :-------------------------------------------------------------------------------: |
|     `android:hint`      |               Placeholder text that disappears when the user types                |
|   `android:inputType`   | Changes the keyboard type (`textPassword`, `number`, `textEmailAddress`, `phone`) |
|   `android:maxLength`   |                      Limits the number of characters allowed                      |
| `android:textColorHint` |                                    Hint color                                     |
|  `android:imeOptions`   |                                  Keyboard action                                  |
|  `android:singleLine`   |                                  One-line input                                   |

**TextInputLayout (Material)**

|          Attribute          |                       Description                       |
| :-------------------------: | :-----------------------------------------------------: |
|      `app:hintEnabled`      | Wraps an EditText to provide a floating label animation |
|    `app:boxStrokeColor`     |            Color of the border when focused             |
| `app:passwordToggleEnabled` | Adds an "eye" icon to show/hide passwords automatically |

### Button

Triggers an **action when clicked**.

|          Attribute          |                    Description                    |
| :-------------------------: | :-----------------------------------------------: |
|       `android:text`        |                   Button label                    |
|    `android:textAllCaps`    |                  Uppercase text                   |
|    `android:background`     |                Background drawable                |
|      `android:onClick`      | (Legacy) Name of the Java method to call on click |
| `android:stateListAnimator` |                   Ripple effect                   |

**ImageButton**

|      Attribute       |                                        Description                                        |
| :------------------: | :---------------------------------------------------------------------------------------: |
|    `android:src`     |                                 The image/icon to display                                 |
| `android:scaleType`  |                      How the image fits (`centerCrop`, `fitCenter`)                       |
| `android:background` | Often set to `?attr/selectableItemBackgroundBorderless` for a ripple effect without a box |

### CheckBox / RadioButton / Switch

Used for **boolean or option selection**.

|      Attribute       |                      Description                      |
| :------------------: | :---------------------------------------------------: |
|  `android:checked`   |                     Default state                     |
| `android:buttonTint` |                      Icon color                       |
|    `android:text`    |                         Label                         |
|   `android:thumb`    |  (Switch) The custom drawable for the moving circle   |
|   `android:track`    | (Switch) The custom drawable for the background track |

**Difference?**

|  Component  |      Behavior      |
| :---------: | :----------------: |
|  CheckBox   | Multiple selection |
| RadioButton |  Single selection  |
|   Switch    |  On / Off toggle   |

### ImageView

Displays **images or icons**.

|          Attribute           |                                                                                   Description                                                                                   |
| :--------------------------: | :-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|        `android:src`         |                                                                  The source image (drawable or standard icon)                                                                   |
|     `android:scaleType`      | Critical property. `centerCrop` (fills view, crops edges (good for photos)). `fitCenter` (shows full image, adds whitespace). `fitXY` (stretches image (distorts aspect ratio)) |
|        `android:tint`        |                                                                                  Color overlay                                                                                  |
| `android:contentDescription` |                                                                     Text for screen readers (Accessibility)                                                                     |
|  `android:adjustViewBounds`  |                                                                                   Auto resize                                                                                   |

### ProgressBar

Shows **loading state**.

|        Attribute        |   Description    |
| :---------------------: | :--------------: |
| `android:indeterminate` | Infinite loading |
|      `android:max`      |    Max value     |
|   `android:progress`    |  Current value   |
| `android:progressTint`  |      Color       |

## 4. Layouts

In Android, a **Layout** is a special type of `ViewGroup` that:

- Defines how child views are positioned
- Controls size, alignment, spacing
- Acts as a container for UI components (TextView, Button, ImageView…)

Choosing the right layout is critical for both **UI design flexibility** and **application performance**.

### Common Layout Parameters

**1. `layout_width` and `layout_height`**

```xml
android:layout_width="match_parent"
android:layout_height="wrap_content"
```

|     Value      |         Meaning          |
| :------------: | :----------------------: |
| `match_parent` |   Fill available space   |
| `wrap_content` |     Fit content size     |
|   `dp` value   | Fixed size (e.g. `48dp`) |

**2. `layout_margin` and `padding`**

```xml
android:layout_margin="16dp"
android:padding="12dp"
```

- **margin** - space **outside** the view
- **padding** - space **inside** the view

### ConstraintLayout (Recommended Standard)

```xml
androidx.constraintlayout.widget.ConstraintLayout
```

This is the most powerful and flexible layout in modern Android development. It allows you to create large, complex layouts with a **flat view hierarchy** (no nested layouts), which improves rendering performance.

- **How it works:** You define the position of a view by creating "constraints" (connections) to other views or the parent container.
- **Key Attributes:**
  - `app:layout_constraintStart_toStartOf`: Aligns the left side (start) of the view.
  - `app:layout_constraintTop_toBottomOf`: Puts the view below another view.
  - `app:layout_constraintDimensionRatio`: Sets aspect ratio (e.g., "16:9").
  - `app:layout_constraintHorizontal_bias`: Adjusts position (0.0 to 1.0) if constraints exist on both sides.

```xml
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <Button
        android:id="@+id/btnA"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Button A"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/btnB"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Button B"
        app:layout_constraintStart_toEndOf="@id/btnA"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="@id/btnA" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Note:** `0dp` in ConstraintLayout means "Match Constraint", filling the available space defined by constraints.

### LinearLayout

```xml
android.widget.LinearLayout
```

A layout that aligns all children in a single direction, either vertically or horizontally. It is simple and very commonly used for list items or simple forms.

- **How it works:** It stacks views one after another.
- **Key Attributes:**
- `android:orientation`: "vertical" (column) or "horizontal" (row).
- `android:layout_weight`: Distributes remaining empty space among children based on a weight value (e.g., giving one view 50% of the screen and another 50%).
- `android:gravity`: Aligns the content inside the layout.

```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Header" />

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="#E0E0E0" />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Footer Button" />

</LinearLayout>
```

### FrameLayout

```xml
android.widget.FrameLayout
```

The simplest layout object. It is designed to block out an area on the screen to display a single item.

- **How it works:** It acts as a stack (Z-index). The first child is at the bottom, and the next child is drawn on top of the previous one.
- **Use Cases:**
  - Displaying a Fragment container.
  - Showing a loading spinner (ProgressBar) on top of the main content.
  - Overlaying text on an image.
- **Key Attributes:**
  - `android:layout_gravity`: Defines where the child is positioned within the frame (e.g., `center`, `bottom|end`).

```xml
<FrameLayout
    android:layout_width="match_parent"
    android:layout_height="200dp">

    <ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:src="@drawable/background_image"
        android:scaleType="centerCrop" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:text="Overlaid Text"
        android:textColor="#FFFFFF" />

</FrameLayout>
```

### RelativeLayout (Legacy)

```xml
android.widget.RelativeLayout
```

Before `ConstraintLayout` existed, this was the go-to layout for complex designs. It positions views relative to each other.

- **Status: Legacy.** It is still supported but performance is lower than `ConstraintLayout` because it requires two measurement passes. Use `ConstraintLayout` for new projects.
- **Key Attributes:**
  - `android:layout_below`, `android:layout_toRightOf`.
  - `android:layout_alignParentBottom="true"`.

### GridLayout

```xml
android.widget.GridLayout
```

`GridLayout` places its children in a rectangular grid of unlimited rows and columns. Unlike `TableLayout`, it is flatter and more efficient because it does not require nested "Row" views.

- **Key Features:**
  - You define the number of rows and columns explicitly.
  - Views can **span** multiple rows or columns (like merging cells in Excel).
  - It is excellent for creating dashboards, calculator interfaces, or fixed-grid menus.
- **Key Attributes:**
  - `android:rowCount`: Total number of rows.
  - `android:columnCount`: Total number of columns.
  - `android:layout_rowSpan`: How many rows a specific child view should occupy.
  - `android:layout_columnSpan`: How many columns a specific child view should occupy.

```xml
<GridLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:rowCount="2"
    android:columnCount="2">

    <Button
        android:text="1"
        android:layout_row="0"
        android:layout_column="0" />

    <Button
        android:text="2"
        android:layout_row="0"
        android:layout_column="1" />

    <Button
        android:text="Long Button"
        android:layout_row="1"
        android:layout_column="0"
        android:layout_columnSpan="2"
        android:layout_gravity="fill_horizontal" />

</GridLayout>
```

### TableLayout

`TableLayout` arranges its children into rows and columns, similar to an HTML `<table>`.

- **Structure:**
  - The parent is `TableLayout`.
  - The children are usually `TableRow` objects.
  - Views inside a `TableRow` form the cells.
- **Behavior:**
  - The width of a column is defined by the widest row in that column.
  - It is rigid compared to `ConstraintLayout` or `GridLayout`.
- **Key Attributes:**
  - `android:stretchColumns`: Indices of columns to stretch to fill empty space (e.g., "1" stretches the second column, "\*" stretches all).
  - `android:shrinkColumns`: Indices of columns that can shrink if content is too wide.
  - `android:collapseColumns`: Indices of columns to hide completely.

```xml
<TableLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:stretchColumns="1"> <TableRow>
        <TextView android:text="Name:" android:padding="8dp"/>
        <EditText android:hint="Enter Name" />
    </TableRow>

    <TableRow>
        <TextView android:text="Email:" android:padding="8dp"/>
        <EditText android:hint="Enter Email" />
    </TableRow>

</TableLayout>
```

### ScrollView

```xml
android.widget.ScrollView
```

A container that allows the view hierarchy to be scrolled vertically.

- **Crucial Rule:** A ScrollView can only have **ONE direct child**.
  - If you want to scroll a list of items, you must wrap them in a `LinearLayout` (or `ConstraintLayout`), and then put that layout inside the `ScrollView`.
- **Performance Warning:** Do not use `ScrollView` for very long lists (like 100+ items). It inflates all views at once, causing memory issues. Use `RecyclerView` for long lists.
- **Variants:**
  - `HorizontalScrollView`: For scrolling left/right.
  - `NestedScrollView`: A modern version (from AndroidX) that supports nested scrolling (e.g., a scrollable list inside a scrollable page). **Recommended over standard ScrollView**.

```xml
<ScrollView
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <TextView android:text="Item 1" android:textSize="24sp" />
        <TextView android:text="Item 2" android:textSize="24sp" />
        <TextView android:text="Item 3" android:textSize="24sp" />
        </LinearLayout>

</ScrollView>
```

### CoordinatorLayout (Advanced Material Design)

```xml
androidx.coordinatorlayout.widget.CoordinatorLayout
```

A super-powered `FrameLayout` meant for top-level application decorations. It is essentially an event handler that enables complex interactions between child views.

- **How it works:** It relies on a concept called **Behaviors**. A child view (like a Button) can "listen" to what another child view (like a `ScrollView`) is doing and react automatically.
- **Use Cases:**
  - **Collapsing Toolbars:** The header image shrinks and fades into a standard Toolbar as you scroll down.
  - **Floating Action Button (FAB) Movement:** If a "Snackbar" (popup message) appears at the bottom, the FAB automatically moves up so it isn't covered.
  - **Sticky Headers:** Headers that stick to the top until the section ends.o-dismiss behavior.

**Detailed Structure for Collapsing Toolbar**

This is the most common implementation pattern.

1. **CoordinatorLayout:** The root parent.
2. **AppBarLayout:** A vertical layout that handles the scrolling events.
3. **CollapsingToolbarLayout:** A wrapper specifically designed to shrink/expand.
4. **NestedScrollView (or RecyclerView):** The content body that triggers the scrolling.

```xml
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar">

        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:contentScrim="?attr/colorPrimary"
            app:layout_scrollFlags="scroll|exitUntilCollapsed">

            <ImageView
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:src="@drawable/header_image"
                android:scaleType="centerCrop"
                app:layout_collapseMode="parallax" />

            <androidx.appcompat.widget.Toolbar
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:layout_collapseMode="pin" />

        </com.google.android.material.appbar.CollapsingToolbarLayout>

    </com.google.android.material.appbar.AppBarLayout>

    <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Long content goes here..."
            android:padding="16dp" />

    </androidx.core.widget.NestedScrollView>

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/ic_add"
        android:layout_gravity="bottom|end"
        android:layout_margin="16dp" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

## 5. Activity

An **Activity** is the fundamental component of an Android app that provides a screen with which users can interact in order to do something (e.g., dial the phone, take a photo, send an email, or view a map).

An Activity is responsible for:

- Rendering UI (`setContentView`)
- Handling user interaction
- Managing lifecycle state
- Navigating to other screens
- Coordinating with Fragments

The **Activity Lifecycle** is a set of states an activity goes through, from the moment it is created until it is destroyed and reclaimed by the system. Understanding this is crucial for managing resources (memory, battery) and preventing crashes.

### The Lifecycle Callbacks

Android provides a series of callback methods that you can override to execute code at specific stages. You **must** call the super implementation for each of these methods.

```java
onCreate()
   ↓
onStart()
   ↓
onResume()
   ↓
[ Activity Running ]
   ↓
onPause()
   ↓
onStop()
   ↓
onDestroy()
```

**1. `onCreate(Bundle)`**

- **State:** Created.
- Called when the system first creates the activity. This triggers only **once** per lifecycle instance.
- **Action:** Perform basic application startup logic that should happen only once for the entire life of the activity (e.g., calling `setContentView()`, initializing variables, binding data to lists).
- **Note:** Avoid heavy operations here.

**2. `onStart()`**

- **State:** Started.
- Called when the activity becomes **visible** to the user but is not yet interactive.
- **Action:** Initialize code that maintains the UI (e.g., registering a `BroadcastReceiver` that updates the UI).

**3. `onResume()`**

- **State:** Resumed (Running).
- Called when the activity moves to the foreground and is **interactive** (accepting user input). The activity is at the top of the stack.
- **Action:** Start animations, open camera preview, resume audio playback, restart paused tasks.

**4. `onPause()`**

- **State:** Paused.
- Called when the user is leaving the activity (it is still partially visible, e.g., a transparent dialog opens on top, or in Multi-Window mode).
- **Action:** Pause animations, stop camera preview, commit unsaved changes (lightweight only). **Do not** do heavy work (like database writes) here, or the transition to the next screen will be slow.

**5. `onStop()`**

- **State:** Stopped.
- Called when the activity is **no longer visible** to the user (e.g., user pressed Home or opened a new full-screen app).
- **Action:** Release heavy resources (e.g., database connections, unregister receivers) to save memory/battery.

**6. `onRestart()`**

- **State:** Restarting.
- Called after your activity has been stopped, prior to it being started again.
- **Action:** Usually empty, but used if you need to know specifically that the user is "coming back" rather than starting fresh.

**7. `onDestroy()`**

- **State:** Destroyed.
- Called before the activity is destroyed. This is the final call.
- **Cause:** User pressed "Back", `finish()` was called, or the system is killing the process to save memory.
- **Action:** Clean up everything (threads, heavy objects) to prevent memory leaks.

### Visualizing the Lifecycle Scenarios

**Scenario A: App Launch**

- User taps app icon.
- `onCreate()` -> `onStart()` -> `onResume()`
- App is running.

**Scenario B: User presses "Home" Button**

- App is running (`onResume`).
- `onPause()` -> `onStop()`
- App is in background. (Data is saved in memory).

**Scenario C: User returns to App (from Recent Apps)**

- App was stopped (`onStop`).
- `onRestart()` -> `onStart()` -> `onResume()`
- App is running again.

**Scenario D: User presses "Back" Button**

- App is running (`onResume`).
- `onPause()` -> `onStop()` -> `onDestroy()`
- Activity is dead. (If the user opens it again, it starts from `onCreate`).

**Scenario E: Configuration Change (Screen Rotation)**

This is the most critical scenario for bugs. When you rotate the phone, the activity is destroyed and recreated to load the new layout (landscape/portrait). It can be triggered when language change or dark mode change.

- `onPause()` -> `onStop()` -> `onDestroy()`
- **IMMEDIATELY FOLLOWED BY:** `onCreate()` -> `onStart()` -> `onResume()`
- **Note:** All local variables are lost unless saved using `onSaveInstanceState` or ViewModels.

### Handling State Loss (`onSaveInstanceState`)

As mentioned in **Scenario E** (Rotation), Android destroys the activity when the configuration changes. To prevent data loss (e.g., text typed into an `EditText` that doesn't have an ID, or a game score), you use `onSaveInstanceState`.

**1. Saving Data (Before Destroy)**

```java
@Override
protected void onSaveInstanceState(Bundle outState) {
    // Save custom values into the Bundle
    outState.putInt("SCORE_KEY", currentScore);
    outState.putString("TEXT_KEY", "Some text");

    // Always call super last
    super.onSaveInstanceState(outState);
}
```

**2. Restoring Data (In `onCreate` or `onRestoreInstanceState`)**

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState != null) {
        // The activity is being recreated (rotated)
        currentScore = savedInstanceState.getInt("SCORE_KEY");
    } else {
        // The activity is starting for the first time
        currentScore = 0;
    }
}
```

**3. `onSaveInstanceState` vs. `SharedPreferences`**

While both are used to save data, they serve completely different purposes regarding **how long** the data lasts and **where** it is stored.

|     Feature      |                                   onSaveInstanceState                                    |                         SharedPreferences                          |
| :--------------: | :--------------------------------------------------------------------------------------: | :----------------------------------------------------------------: |
| Primary Purpose  |           "Restoring UI state during configuration changes (e.g., rotation)."            |          Saving user settings or small data permanently.           |
|     Lifespan     | **Temporary**. Data is lost if the user completely closes the app or restarts the phone. |   **Permanent**. Data survives app closures and phone restarts.    |
| Storage Location |                       **Memory (RAM)**. Held in a `Bundle` object.                       |             **Disk (Storage)**. Saved as an XML file.              |
|    Data Types    |                    "Primitives (int, boolean) & Parcelable objects."                     |            "Primitives (int, boolean, string, float)."             |
|   Performance    |                              Very Fast (Memory operation).                               |                    Slower (Disk I/O operation).                    |
|  When to save?   |             Automatically called by the system before killing the activity.              | "You must explicitly call it when data changes (e.g., `apply()`)." |

**4. When to use `onSaveInstanceState`?**

Use this for **Transient UI State**. This is data that is only relevant right now while the user is looking at the screen. If the user closes the app and comes back a week later, they would not expect this data to still be there.

**Common Use Cases:**

- **Screen Rotation:** The user rotates the phone, and you need to preserve variables that aren't automatically saved by View IDs.
- **System Kill:** The user puts the app in the background, interacts with other apps, and Android kills your app process to free up memory. When the user returns, you need to restore the screen exactly as they left it.
- **Search Queries:** The user typed a search term but hasn't pressed enter yet.

**5. When to use `SharedPreferences`?**

Use this for **Persistent Application Data**. This is data that the user expects to remain saved even if they turn off their phone and come back next month.

**Common Use Cases:**

- **User Settings:** Dark Mode vs Light Mode, Notification On/Off.
- **Login State:** Saving a lightweight "Remember Me" token or username.
- **High Scores:** Keeping the highest score achieved in a game.
- **First Run Check:** Checking if the user is opening the app for the first time (to show a tutorial).

```java
// 1. Save data (e.g., inside a button click)
SharedPreferences sharedPref = getSharedPreferences("MyGamePrefs", MODE_PRIVATE);
SharedPreferences.Editor editor = sharedPref.edit();
editor.putInt("HIGH_SCORE", 9999);
editor.putString("USER_NAME", "PlayerOne");
editor.apply(); // writes to disk asynchronously

// 2. Read data (e.g., in onCreate)
SharedPreferences sharedPref = getSharedPreferences("MyGamePrefs", MODE_PRIVATE);
int highScore = sharedPref.getInt("HIGH_SCORE", 0); // 0 is default if not found
String user = sharedPref.getString("USER_NAME", "Unknown");
```

### Launch Modes

Defined in Manifest:

```xml
android:launchMode="singleTop"
```

|       Mode       |       Behavior        |
| :--------------: | :-------------------: |
|    `standard`    |        Default        |
|   `singleTop`    |    Reuse if on top    |
|   `singleTask`   | One instance per task |
| `singleInstance` |       Own task        |

## 6. Fragment

A **Fragment** represents a reusable portion of an application's user interface. You can think of a Fragment as a modular section of an Activity, which has its own lifecycle, receives its own input events, and which you can add or remove while the activity is running.

**Key Rule:** A Fragment must always be embedded within an **Activity** (or another Fragment). It cannot exist independently.

### Why use Fragments?

- **Modularity:** You can reuse the same Fragment in multiple Activities (e.g., a Login Fragment used in both a Welcome Screen and a Settings Screen).
- **Responsiveness:** Essential for Tablet layouts.
  - **Phone:** Activity A shows a list, Activity B shows details.
  - **Tablet:** One Activity shows **Fragment A** (List) on the left and **Fragment B** (Details) on the right.
- **Dynamic UI:** You can swap UI components without reloading the entire screen (Activity).

**Fragment vs Activity**

|         Activity         |          Fragment           |
| :----------------------: | :-------------------------: |
| Represents a full screen | Represents part of a screen |
|     App entry point      |     Cannot exist alone      |
|      Managed by OS       |     Managed by Activity     |
|          Heavy           |         Lightweight         |

### The Fragment Lifecycle

The Fragment lifecycle is closely tied to the host Activity, but it has its own specific methods.

|           Method           |                                                      Description & Usage                                                       |
| :------------------------: | :----------------------------------------------------------------------------------------------------------------------------: |
|        `onAttach()`        |                                 Called when the fragment has been associated with the Activity                                 |
|        `onCreate()`        |                                   Initialize non-graphical components (similar to Activity)                                    |
|      `onCreateView()`      |                     **Critical**. This is where you inflate the XML layout. You must return a `View` here                      |
|     `onViewCreated()`      | Called immediately after `onCreateView`. This is the best place to initialize UI elements (`findViewById`, `onClickListeners`) |
| `onStart()` / `onResume()` |                                     Fragment becomes visible and active (tied to Activity)                                     |
|  `onPause()` / `onStop()`  |                                          Fragment is no longer interacting or visible                                          |
|     `onDestroyView()`      |                                   The view hierarchy associated with the fragment is removed                                   |
|        `onDetach()`        |                                        The fragment is disassociated from the Activity                                         |

### Managing Fragments (The `FragmentManager`)

To add, remove, or replace fragments, you use the `FragmentManager` and `FragmentTransaction`.

- `add()`: Puts a fragment into the container.
- `replace()`: Removes whatever is currently in the container and adds the new fragment. (Most common).
- `remove()`: Removes a fragment.
- `addToBackStack()`: Very important. If you call this, when the user presses the "Back" button, it reverses the transaction (removes the fragment) instead of closing the Activity.

```java
Fragment newFragment = new MyFragment();

// Get the FragmentManager
FragmentManager fragmentManager = getSupportFragmentManager();

// Start a transaction
fragmentManager.beginTransaction()
    .replace(R.id.fragment_container, newFragment) // Replace content of container
    .addToBackStack(null) // Optional: Back button navigates back to previous state
    .commit(); // Apply changes
```

### Step-by-Step Example

Let's build a simple app where clicking a button dynamically loads a Fragment into the Activity.

**Step A: Create the Fragment Layout**

File: `res/layout/fragment_example.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="#E0F7FA"
    android:gravity="center">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello! I am a Fragment."
        android:textSize="24sp"
        android:textStyle="bold" />

</LinearLayout>
```

**Step B: Create the Fragment Java Class**

File: `java/com/example/app/ExampleFragment.java`

```java
package com.example.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ExampleFragment extends Fragment {

    // Required empty public constructor
    public ExampleFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_example, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize UI logic here (e.g., button clicks inside the fragment)
    }
}
```

**Step C: Update Activity Layout (The Container)**

We need a placeholder (Container) where the fragment will be displayed. We usually use a `FrameLayout` or `FragmentContainerView`. File: `res/layout/activity_main.xml`

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <Button
        android:id="@+id/btnLoadFragment"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Load Fragment" />

    <FrameLayout
        android:id="@+id/fragment_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="16dp"
        android:background="#EEEEEE"/>

</LinearLayout>
```

**Step D: Logic in MainActivity**

File: `java/com/example/app/MainActivity.java`

```java
package com.example.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    Button btnLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoad = findViewById(R.id.btnLoadFragment);

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ExampleFragment());
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        // 1. Get Manager
        FragmentManager fm = getSupportFragmentManager();
        
        // 2. Start Transaction
        FragmentTransaction ft = fm.beginTransaction();
        
        // 3. Replace the content of the container with the new fragment
        ft.replace(R.id.fragment_container, fragment);
        
        // 4. (Optional) Add to back stack so "Back" button removes fragment
        ft.addToBackStack(null);

        // 5. Commit changes
        ft.commit();
    }
}
```

### Sending Data to a Fragment (Arguments)

**Do not** create custom constructors for Fragments (e.g., `new MyFragment(String data)`). Android may destroy and recreate the fragment, and your custom constructor will be lost, causing a crash.

**Correct Way:** Use `Bundle`

**1. Creating the Fragment instance:**

```java
ExampleFragment fragment = new ExampleFragment();
Bundle args = new Bundle();
args.putString("key_name", "John Doe");
fragment.setArguments(args); // Attach data

// Now perform the transaction...
```

**2. Reading data inside the Fragment (`onCreate` or `onViewCreated`):**

```java
@Override
public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
        String name = getArguments().getString("key_name");
        // Use the name...
    }
}
```
