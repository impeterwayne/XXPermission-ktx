# xxpermission-ktx

A Kotlin DSL for XXPermissions, making it easier to handle Android permissions.

## Acknowledgements

This library is a fork of and inspired by the following projects:

- [XXPermissions](https://github.com/getActivity/XXPermissions) by getActivity
- [permissions-ktx](https://github.com/marcelpinto/permissions-ktx) by marcelpinto

## Installation
Add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    implementation 'io.github.impeterwayne:xxpermission-ktx:1.0.2'
}
```

## Usage

```kotlin
xxPermissions {
    permissions(PermissionLists.getPostNotificationsPermission())
    onDoNotAskAgain { permissions, userResult ->
        // Show a dialog to the user explaining why the permission is needed
        // and guide them to the app settings.
        // userResult.onResult(true) will take the user to settings.
    }
    onShouldShowRationale { shouldShowRationaleList, onUserResult ->
        // Show a dialog explaining why you need the permission.
        // onUserResult.onResult(true) will proceed with the permission request.
    }
    onResult { allGranted, grantedList, deniedList ->
        // Handle the result
    }
}
```

### Request multiple permissions

```kotlin
xxPermissions {
    permissions(
        PermissionLists.getSystemAlertWindowPermission(true),
        PermissionLists.getPostNotificationsPermission()
    )
    onShouldShowRationale { permissions, userResult ->
        // Explain why these permissions are needed.
        // userResult.onResult(true) will continue the request flow.
    }
    onDoNotAskAgain { permissions, userResult ->
        // Explain that some permissions were permanently denied
        // and optionally send the user to app settings.
        // userResult.onResult(true) will open settings.
    }
    onResult { allGranted, grantedList, deniedList ->
        if (allGranted) {
            // All permissions granted.
        } else {
            // Handle partially granted or denied permissions.
        }
    }
}
```
