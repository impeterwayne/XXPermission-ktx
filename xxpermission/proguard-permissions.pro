# Prevent obfuscation of method names in the IStartActivityDelegate and IFragmentMethodNative interfaces and their implementation classes
# This is because the framework implements these two interfaces with both the Fragment from the Support library and the Fragment from the App package.
# The purpose of this is to abstract different Fragments, so the framework does not need to care about or determine which specific Fragment is requesting permissions.
# However, this approach can cause a problem: some users reported crashes after obfuscation. After investigation, it was found that the outer layer passed a regular Activity object
# instead of a FragmentActivity object. The framework then used the Fragment from the App package to request permissions, which caused the issue.
# If the outer layer passes a FragmentActivity object, the framework uses the Fragment from the Support library to request permissions, and the problem does not occur.
# Related issue: https://github.com/getActivity/XXPermissions/issues/371
# The reason for adding this obfuscation rule is to prevent the compiler from obfuscating these methods in the Fragment from the Support library.
# Because the framework internally uses both the Fragment from the App package and the Fragment from the Support library to request permissions,
# if these methods are obfuscated, a problem will occur: the methods in the Fragment from the Support library will be obfuscated,
# but the Fragment from the App package is a system class and will definitely not be obfuscated. This will cause method names to not match during calls, resulting in AbstractMethodError.
# Either do not obfuscate, or if you do, obfuscate both together so the method names remain consistent and calls can match (the corresponding method can be found).
# The problem is that one side cannot be obfuscated, which is the Fragment class from the App package. So the only solution is to not obfuscate these method names.
-keepclassmembers interface com.hjq.permissions.start.IStartActivityDelegate {
    <methods>;
}
-keepclassmembers interface com.hjq.permissions.fragment.IFragmentMethodNative {
    <methods>;
}

# Prevent obfuscation of the getActivity method name in the Fragment from the Support library
# You may have a few questions here, which I will answer one by one:
# 1. Isn't the obfuscation rule for the IFragmentMethodNative interface above already covering the getActivity method?
#    Why add a separate, seemingly redundant obfuscation rule here? Isn't this unnecessary?
#    This is because this method is special: during obfuscation, the compiler did not correctly recognize this method,
#    resulting in the getActivity method defined in the Fragment from the Support library being obfuscated,
#    but the getActivity method defined in IFragmentMethodNative was not obfuscated.
#    This is because IFragmentMethodNative defines A getActivity(),
#    while the Fragment from the Support library defines FragmentActivity getActivity().
#    If you do not prevent obfuscation of the getActivity method name in Fragment, it will be obfuscated.
# 2. Why only write the obfuscation rule for the Support library? Are you sure this rule works for AndroidX?
#    The answer is: there is no problem, feel free to use it. I have tested it for everyone and it works perfectly. If you don't believe it, you can decompile the apk and check.
#    When android.enableJetifier=true is enabled, the compiler will automatically convert the package name to AndroidX,
#    so there is no need to write a separate obfuscation rule for AndroidX here. Doing so would be redundant and meaningless.

#-keepclassmembers class androidx.fragment.app.Fragment {
#    androidx.fragment.app.FragmentActivity getActivity();
#}