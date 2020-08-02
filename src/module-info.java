module myPixelEditorAttempt
{
	requires java.desktop;
	// The unstable module name warning: https://stackoverflow.com/a/46742802/11009247
	// It cannot be fixed by me, reason in the link above: The JAR file doesn't define Automatic-Module-Name, so java uses the fallback method, which is intrinsically unstable
	requires Filters;
}