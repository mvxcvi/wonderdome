Developing Wonderdome
=====================

## Leiningen

Leiningen is the Clojure build tool, which you will need to develop the
Wonderdome code. It's fairly good at bootstrapping, so you should just need to
drop the script somewhere on your `$PATH`. Under the hood, Leiningen uses Maven
for dependency management and specification, borrowing from the primary Java
ecosystem.

## REPL

Most of your testing will happen from the Clojure REPL, an interactive coding
environment. Once Leiningen is installed, navigate to the project directory and
launch the REPL:

```bash
lein repl
```

The first time you run this, you should see it download all of the dependencies
used by the system. You should eventually be greeted by a `user=>` prompt. From
here, you can type Clojure statements an immediately see the results:

```clojure
(println "Hello world!")
```

## Testing

This project splits the Clojure source files into three top-level directories:
- `src` contains the main source files for the system
- `dev` contains sources only relevant when developing locally
- `test` contains unit tests and test harnesses

Try running the unit tests and make sure everything passes:

```bash
lein test
```

If all goes well, switch to the REPL and try running the color test harness:

```clojure
(require '[org.playasophy.wonderdome.util.color-test :as color-test])
(color-test/color-harness)
```

You should see a window pop up which demonstrates the color functionality
available. If not, there is likely an issue with the environment the REPL is
running in - check to make sure it has access to your `$DISPLAY` (on linux).

## Local Simulation

Finally, try running the local Wonderdome system! In your REPL:

```clojure
(go!)
```

This should pop up a window showing the 3D rendering of the geodesic dome and
the pixel strips.

The `go!` function and it's siblings are defined by the `dev/user.clj` file,
which sets up the default `user` namespace the REPL launches into. These
functions give you an easy way to tear down and reconstruct the system to load
new code changes.

- `init!` populates the `system` var with a fresh non-running system
- `start!` hooks the system components together and runs it
- `stop!` halts the system
- `go!` is the same as `init!` followed by `start!`
- `reload!` stops the running system, reloads changed code, and calls `go!`

### Button Controls

Many visualization modes will want to respond to button-press events, so during
local development if you don't have a USB SNES gamepad you can press keys
directly in the Processing display. The arrow keys will send X and Y-axis events
to simulate the d-pad, and the A, B, X, Y, L, and R letters send the
corresponding button press events. The enter (return) and spacebar send start
and select buttons, respectively.

Due to the nature of keyboard auto-repeats, the buttons won't function exactly
the same way as the gamepad. In particular, the non-axis buttons may
occasionally send repeated presses, and you won't see any `:button/release`
events. Additionally, the axis buttons will have an initial ~500 ms lag before
the keyboard's repeat kicks in.