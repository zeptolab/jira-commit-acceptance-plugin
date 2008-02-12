#include "stdio.h"
#include "stdlib.h"

int main(int argc, char **argv) {
	if (argc < 2)
		return 1; /* We need at least one argument to know what we should print */

	if (0 == strcmp("author", argv[1]) && getenv("COMMITER"))
		printf("%s\n", getenv("COMMITER"));
	if (0 == strcmp("log", argv[1]) && getenv("COMMITMESSAGE"))
		printf("%s\n", getenv("COMMITMESSAGE"));
	return 0;
}
