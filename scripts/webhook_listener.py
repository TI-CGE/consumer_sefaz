import os
import subprocess
from urllib.parse import urlparse, parse_qs
from http.server import HTTPServer, BaseHTTPRequestHandler

SCRIPT = os.path.join(os.path.dirname(__file__), "deploy.sh")
REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        token = os.environ.get("WEBHOOK_SECRET")
        if token:
            parsed = urlparse(self.path)
            qs = parse_qs(parsed.query)
            if qs.get("token", [None])[0] != token:
                self.send_response(403)
                self.end_headers()
                return
        try:
            subprocess.run(
                ["bash", SCRIPT],
                check=True,
                cwd=REPO_ROOT,
                env={**os.environ, "PATH": os.environ.get("PATH", "")},
            )
            self.send_response(200)
        except subprocess.CalledProcessError:
            self.send_response(500)
        self.end_headers()

    def log_message(self, format, *args):
        pass


if __name__ == "__main__":
    port = int(os.environ.get("WEBHOOK_PORT", "9000"))
    HTTPServer(("", port), Handler).serve_forever()
